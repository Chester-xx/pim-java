package service;

import db.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    // Return a user object by username
    public User getUserByUsername(String username) {
        
        // create conn, stmt & bind param
        try (Connection conn = DBConnection.getConnection();    
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            
            stmt.setString(1, username);

            // execute query and process result
            try (ResultSet rs = stmt.executeQuery()) { 
            
                if (rs.next()) { return new User( rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("role"), rs.getString("full_name") ); } 
            
            }
        
        } catch (SQLException e) { e.printStackTrace(); }

        // user not found
        return null;
    
    }

    // Insert new user into db
    public boolean createUser(User user) {
        
        // create conn, stmt & bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)")) {   
            
            stmt.setString(1, user.getUsername());
            // hash password
            stmt.setString(2, PasswordUtil.hash(user.getPassword()));
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());

            // get and return insertion success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        // any failure (conn, prep etc)
        return false;

    }

    // Return all users in db
    public java.util.List<User> getAllUsers() {
        
        // create conn, stmt
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users")) {
        
            // exec query
            try (ResultSet rs = stmt.executeQuery()) { 
                
                // if results exist
                if (rs.next()) { 

                    // create list and add users to it
                    java.util.List<User> users = new java.util.ArrayList<>(); 
                    do { users.add(new User( rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("role"), rs.getString("full_name") )); } 
                    while (rs.next()); return users;

                } 

            }

        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return empty list
        return new java.util.ArrayList<>();

    }

    // Update user by id
    public boolean updateUser(User user) {

        // create conn, stmt & bind params
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET role = ?, full_name = ? WHERE user_id = ?")) {

            stmt.setString(1, user.getRole());
            stmt.setString(2, user.getFullName());
            stmt.setInt(3, user.getUserId());

            // execute update and return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        // failure, return false
        return false;

    }

    // Delete user by id
    public boolean deleteUser(int userId) {
        
        // create conn, stmt & bind param
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            
            stmt.setInt(1, userId);
            
            // execute update and return success
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) { e.printStackTrace(); }
        
        // failure, return false
        return false;

    }

}
