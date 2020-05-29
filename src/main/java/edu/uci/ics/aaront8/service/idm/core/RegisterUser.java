package edu.uci.ics.aaront8.service.idm.core;

import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterUser {

    public static boolean checkEmailInUse(String email){
        boolean emailUsed = false;
        try {
           // boolean emailUsed = false;

            String query =  "SELECT user_id, email" +
                            " FROM user" +
                            " WHERE email LIKE ?;";


            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();

            ServiceLogger.LOGGER.info("Query succeeded.");


            if (rs.next()){
                emailUsed = true;
                String mail = rs.getString("email");
                ServiceLogger.LOGGER.info("Email already in use.");
                return emailUsed;
            }


        ServiceLogger.LOGGER.info("Email NOT in use.");
        return emailUsed;



        } catch (SQLException e){
            //ServiceLogger.LOGGER.warning("Query failed");
            ServiceLogger.LOGGER.warning("Query did not succeed");
           // e.printStackTrace();

        }
        ServiceLogger.LOGGER.info("Email NOT in use.");

        return emailUsed;
    }







}
