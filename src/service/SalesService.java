package service;

import db.DBConnection;
import model.ItemSalesSummary;
import model.Sale;
import model.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesService {

    // Records a new sale and its items in a single transaction, if either fails, the entire operation is rolled back
    public boolean recordSale(Sale sale, List<SaleItem> items) {

        // get conn, disable auto-commit to start transaction
        Connection conn = null;

        try {

            // create conn, disable auto-commit to start transaction
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // start transaction

            // decl gen sale id
            int generatedSaleId;

            // insert sale record, retrieve generated sale_id
            try (PreparedStatement saleStmt = conn.prepareStatement("INSERT INTO sales (total_amount, user_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                
                saleStmt.setDouble(1, sale.getTotalAmount());
                saleStmt.setInt(2, sale.getUserId());
                saleStmt.executeUpdate();

                // retrieve generated sale_id
                try (ResultSet keys = saleStmt.getGeneratedKeys()) {
                
                    // check if a key was generated and retrieve it
                    if (keys.next()) { generatedSaleId = keys.getInt(1); sale.setSaleId(generatedSaleId); } 
                    else { throw new SQLException("Failed to retrieve generated sale_id."); }
                
                }
            
            }

            // insert each sale item, using the generated sale_id
            try (PreparedStatement itemStmt = conn.prepareStatement("INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)")) {
                
                // bind params for each item and add to batch
                for (SaleItem item : items) {
                    
                    itemStmt.setInt(1, generatedSaleId);
                    itemStmt.setInt(2, item.getMedicineId());
                    itemStmt.setInt(3, item.getQuantitySold());
                    itemStmt.setDouble(4, item.getPriceAtSale());
                    
                    // queue to batch
                    itemStmt.addBatch();
                
                }
            
                // execute batch insert for all items
                itemStmt.executeBatch();
            
            }

            // commit transaction if both inserts succeeded
            conn.commit();
            return true;

        } catch (SQLException e) {
            
            e.printStackTrace();

            // rollback if conn is not null
            if (conn != null) { try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); } }
            return false;

        // finally, reset auto-commit to true
        } finally { if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } } }
    
    }

    // Retrieve all sales within a date range
    public List<Sale> getSalesByDateRange(Timestamp from, Timestamp to) {
    
        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sales WHERE sale_date BETWEEN ? AND ?")) {
        
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);

            // exec & map results to list
            ResultSet rs = stmt.executeQuery();
            List<Sale> sales = new ArrayList<>();
        
            // map each row to obj, add to list
            while (rs.next()) {
                Sale sale = new Sale();
                sale.setSaleId(rs.getInt("sale_id"));
                sale.setTotalAmount(rs.getDouble("total_amount"));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
                sale.setUserId(rs.getInt("user_id"));
                sales.add(sale);
            }

            // return list
            return sales;
        
        } catch (SQLException e) { e.printStackTrace(); }
    
        // failure, return empty list
        return new ArrayList<>();
    
    }

    // Return all items for a given sale
    public List<SaleItem> getItemsForSale(int saleId) {

        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sale_items WHERE sale_id = ?")) {
            
            stmt.setInt(1, saleId);

            // exec & map results to list
            ResultSet rs = stmt.executeQuery();
            List<SaleItem> items = new ArrayList<>();

            // map each row to obj, add to list
            while (rs.next()) {
                SaleItem item = new SaleItem();
                item.setSaleItemId(rs.getInt("sale_item_id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setMedicineId(rs.getInt("medicine_id"));
                item.setQuantitySold(rs.getInt("quantity_sold"));
                item.setPriceAtSale(rs.getDouble("price_at_sale"));
                items.add(item);
            }

            // return list
            return items;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return empty list
        return new ArrayList<>();

    }

    // Returns sales report on total quantity sold and total revenue per medicine, highest sellers first
    public List<ItemSalesSummary> getItemWiseSalesReport() {

        // create conn, stmt
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT si.medicine_id, m.name AS medicine_name, SUM(si.quantity_sold) AS total_sold, SUM(si.quantity_sold * si.price_at_sale) AS total_revenue FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id GROUP BY si.medicine_id, m.name ORDER BY total_sold DESC")) {

            // exec & map results to list
            ResultSet rs = stmt.executeQuery();
            List<ItemSalesSummary> report = new ArrayList<>();

            // map each aggregated row to obj, add to list
            while (rs.next()) { report.add(new ItemSalesSummary(rs.getInt("medicine_id"), rs.getString("medicine_name"), rs.getInt("total_sold"), rs.getDouble("total_revenue"))); }

            // return list
            return report;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return empty list
        return new ArrayList<>();

    }
}
