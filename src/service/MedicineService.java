package service;

import db.DBConnection;
import model.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineService {

    // Helper to map a res set row to a Medicine object
    private Medicine mapRow(ResultSet rs) throws SQLException {
        
        // get expiry date & return object mapping
        Date expiry = rs.getDate("expiry_date");
        return new Medicine(rs.getInt("medicine_id"), rs.getString("name"), rs.getString("company"), rs.getString("medicine_type"), rs.getDouble("price"), rs.getInt("quantity_in_stock"), rs.getInt("reorder_level"), expiry != null ? expiry.toLocalDate() : null, rs.getInt("supplier_id"));

    }

    // Return all medicines in db
    public List<Medicine> getAllMedicines() {
        
        // create list, create conn, stmt
        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medicines");
        
        // exec query, it res set & map each row to obj, add to list
        ResultSet rs = stmt.executeQuery()) { 
            
            while (rs.next()) { medicines.add(mapRow(rs)); }
        
        } catch (SQLException e) { e.printStackTrace(); }
        
        // return list
        return medicines;
    
    }

    // Return a single medicine by ID
    public Medicine getMedicineById(int medicineId) {
        
        // create conn, stmt, bind param
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medicines WHERE medicine_id = ?")) {
            
            stmt.setInt(1, medicineId);

            // get res set and map row to obj if found
            ResultSet rs = stmt.executeQuery();    
            if (rs.next()) { return mapRow(rs); }
            
        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return null
        return null;
    
    }

    // Insert new medicine into db
    public boolean addMedicine(Medicine medicine) {
        
        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getCompany());
            stmt.setString(3, medicine.getMedicineType());
            stmt.setDouble(4, medicine.getPrice());
            stmt.setInt(5, medicine.getQuantityInStock());
            stmt.setInt(6, medicine.getReorderLevel());
            stmt.setDate(7, medicine.getExpiryDate() != null ? java.sql.Date.valueOf(medicine.getExpiryDate()) : null);
            stmt.setInt(8, medicine.getSupplierId());

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return false
        return false;

    }

    // Update existing medicine in db
    public boolean updateMedicine(Medicine medicine) {
        
        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE medicines SET name=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, reorder_level=?, expiry_date=?, supplier_id=? WHERE medicine_id=?")) {
            
            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getCompany());
            stmt.setString(3, medicine.getMedicineType());
            stmt.setDouble(4, medicine.getPrice());
            stmt.setInt(5, medicine.getQuantityInStock());
            stmt.setInt(6, medicine.getReorderLevel());
            stmt.setDate(7, medicine.getExpiryDate() != null ? java.sql.Date.valueOf(medicine.getExpiryDate()) : null);
            stmt.setInt(8, medicine.getSupplierId());
            stmt.setInt(9, medicine.getMedicineId());

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return false
        return false;

    }

    // Reduce stock of a medicine after a sale, also checks if stock is sufficient
    public boolean reduceStock(int medicineId, int quantitySold) {

        // check stock is sufficient
        Medicine medicine = getMedicineById(medicineId);
        if (medicine == null || medicine.getQuantityInStock() < quantitySold) { return false; }
        
        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ?")) {
            
            stmt.setInt(1, quantitySold);
            stmt.setInt(2, medicineId);

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return false
        return false;

    }

    // Delete a medicine from db
    public boolean deleteMedicine(int medicineId) {
    
        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM medicines WHERE medicine_id = ?")) {
            
            stmt.setInt(1, medicineId);

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return false
        return false;
    
    }

    // Return medicines that are low in stock, quantity <= reorder level
    public List<Medicine> getLowStockMedicines() {
        
        // create list, create conn, stmt
        List<Medicine> lowStockMedicines = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medicines WHERE quantity_in_stock <= reorder_level");
        ResultSet rs = stmt.executeQuery()) {
            
            // it res set & map each row to obj, add to list
            while (rs.next()) { lowStockMedicines.add(mapRow(rs)); }

            return lowStockMedicines;
        
        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return empty list
        return new ArrayList<>();

    }

    // Return medicines that are expiring within a 30 day period
    public List<Medicine> getExpiringSoonMedicines() {
        
        // create list, create conn, stmt
        List<Medicine> expiringSoonMedicines = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medicines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)");
        ResultSet rs = stmt.executeQuery()) {
        
            // it res set & map each row to obj, add to list
            while (rs.next()) { expiringSoonMedicines.add(mapRow(rs)); }

            return expiringSoonMedicines;
        
        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return empty list
        return new ArrayList<>();
    
    }

}
