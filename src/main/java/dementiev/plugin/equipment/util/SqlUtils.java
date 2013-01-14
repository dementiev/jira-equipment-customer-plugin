package dementiev.plugin.equipment.util;

import com.atlassian.jira.exception.DataAccessException;
import dementiev.plugin.equipment.Customer;
import dementiev.plugin.equipment.Equipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author dmitry dementiev
 */

public class SqlUtils {

    public static Long getGroupIdByName(String groupName) throws DataAccessException, SQLException, ClassNotFoundException {
        Connection conn = SQLHelper.getConnection();
        String sqlQuery = SQLHelper.getSQLProperty("getGroupIdByName");
        PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
        pstmt.setString(1, groupName);
        ResultSet rs = pstmt.executeQuery();
        Long idOfGroup = null;
        while (rs.next()) {
            idOfGroup = rs.getLong(1);
        }
        rs.close();
        pstmt.close();
        conn.close();
        return idOfGroup;
    }

    /* public static Long getUserIdByName(String userName) throws DataAccessException, SQLException {
        Connection conn = SQLHelper.getConnection();
        String sqlQuery = SQLHelper.getSQLProperty("getUserIdByName");
        PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
        pstmt.setString(1, userName);
        ResultSet rs = pstmt.executeQuery();
        Long idOfUser = null;
        while (rs.next()) {
            idOfUser = rs.getLong(1);
        }
        rs.close();
        pstmt.close();
        conn.close();
        return idOfUser;
    }*/

    public static ArrayList<Customer> getAllCustomers() throws ClassNotFoundException, SQLException {
        Connection conn = SQLHelper.getConnection();
        String sqlQuery = SQLHelper.getSQLProperty("getAllCustomers");
        ResultSet rs = null;
        PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
        ArrayList<Customer> customers = new ArrayList<Customer>();
        try {
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt(1));
                customer.setJiraGroupName(rs.getString(2));
                customer.setLevel(rs.getInt(3));
                customers.add(customer);
            }
        } catch (SQLException se) {
            //log.error(se);
            se.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    //      logger.error(e);
                    e.printStackTrace();
                }
            }
        }
        return customers;
    }
    public static ArrayList<Equipment> getAllEquipments() throws ClassNotFoundException, SQLException {
        Connection conn = SQLHelper.getConnection();
        String sqlQuery = SQLHelper.getSQLProperty("getAllEquipments");
        ResultSet rs = null;
        PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
        ArrayList<Equipment> equipments = new ArrayList<Equipment>();
        try {
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setId(rs.getInt(1));
                equipment.setSerial(rs.getString(2));
                equipment.setInv(rs.getString(3));
                equipment.setCustomer(getCustomerById(rs.getInt(4)));
                equipments.add(equipment);
            }
        } catch (SQLException se) {
            //log.error(se);
            se.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    //      logger.error(e);
                    e.printStackTrace();
                }
            }
        }
        return equipments;
    }
    public static ArrayList<Equipment> getEquipmentOfGroup(int groupId) throws DataAccessException, SQLException, ClassNotFoundException {
        Connection conn = SQLHelper.getConnection();
        String sqlQuery = SQLHelper.getSQLProperty("getEquipmentOfGroup");
        PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
        pstmt.setInt(1, groupId);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Equipment> equipmentArrayList = new ArrayList<Equipment>();
        while (rs.next()) {
            Equipment equipment = new Equipment();
            equipment.setId(rs.getInt(1));
            equipment.setSerial(rs.getString(2));
            equipment.setInv(rs.getString(3));
            equipmentArrayList.add(equipment);
        }
        rs.close();
        pstmt.close();
        conn.close();
        return equipmentArrayList;
    }

    public static Customer getCustomerById(int id) throws ClassNotFoundException, SQLException {
        Connection conn = SQLHelper.getConnection();
        String sqlQuery = SQLHelper.getSQLProperty("getCustomerById");
        PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        Customer customer = new Customer();
        while (rs.next()) {
            customer.setId(rs.getInt(1));
            customer.setJiraGroupName(rs.getString(2));
            customer.setLevel(rs.getInt(3));
        }
/*       rs.close();
        pstmt.close();
        conn.close();*/
        return customer;
    }
}