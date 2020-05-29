package edu.uci.ics.aaront8.service.idm.core;

import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.idm.security.Token;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LoginUser {


    public static void updateSession(Token sessionID, String email, Timestamp timeCreated, Timestamp lastUsed, Timestamp expr_time){


        try{

            String query = "UPDATE session" +
                           " SET status = ?" +
                           " WHERE status = ?;";

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, "REVOKED");
            ps.setString(2, "ACTIVE");
            ServiceLogger.LOGGER.info("Revoking all active sessions");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Successfully revoked");

            String query1 = "INSERT INTO session (session_id, email, status, time_created, last_used, expr_time) VALUES (?, ?, ?, ?, ?, ?);";

            PreparedStatement ps1 = IDMService.getCon().prepareStatement(query1);
            ps1.setString(1, sessionID.toString());
            ps1.setString(2, email);
            ps1.setInt(3, 1);
            ps1.setTimestamp(4, timeCreated);
            ps1.setTimestamp(5, lastUsed);
            ps1.setTimestamp(6, expr_time);

            ServiceLogger.LOGGER.info("Adding new session");
            ps1.executeUpdate();
            ServiceLogger.LOGGER.info("Successfully added new session");





        } catch (SQLException e){
            ServiceLogger.LOGGER.info("SQL FAILED!");

        }






    }
}
