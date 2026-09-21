package service;

import db.DBConnection;
import model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierService {

    // Return all suppliers from db
    public List<Supplier> getAllSuppliers() {
        
        // create list
        List<Supplier> suppliers = new ArrayList<>();

        // create conn, stmt, and rs
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM suppliers");
        ResultSet rs = stmt.executeQuery()) {

            // it through result set and add to list
            while (rs.next()) {
                suppliers.add(new Supplier( rs.getInt("supplier_id"), rs.getString("name"), rs.getString("contact_person"), rs.getString("phone"), rs.getString("email"), rs.getString("address")));
            }

        } catch (SQLException e) { e.printStackTrace(); }
    
        // return list
        return suppliers;
    
    }

    // Insert new supplier into db
    public boolean addSupplier(Supplier supplier) {
        
        // create conn, stmt
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return false
        return false;
    
    }

    // Update existing supplier in db
    public boolean updateSupplier(Supplier supplier) {
    
        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE supplier_id=?")) {
            
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setInt(6, supplier.getSupplierId());

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return false
        return false;
    
    }

    // Delete a supplier from db
    public boolean deleteSupplier(int supplierId) {

        // create conn, stmt, bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM suppliers WHERE supplier_id=?")) {
            
            stmt.setInt(1, supplierId);

            // exec & return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return false
        return false;
    
    }

}
