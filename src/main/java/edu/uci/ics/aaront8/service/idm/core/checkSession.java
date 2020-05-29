package edu.uci.ics.aaront8.service.idm.core;

import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.configs.ServiceConfigs;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.idm.security.Session;
//import org.glassfish.grizzly.http.util.TimeStamp;
//import org.glassfish.grizzly.http.util.TimeStamp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
@SuppressWarnings("Duplicates")
public class checkSession {

    public static boolean checkExpired(String email, String session_id) {

        boolean expired = false;
        //   boolean revoked = false;
        //  long timeout;

        try {

            ServiceLogger.LOGGER.info("Checking if current request TIME is after expired time (EXPIRED)");
            String query = "SELECT session_id, email, status, time_created, last_used, expr_time" +
                    " FROM session" +
                    " WHERE session_id LIKE ? AND email LIKE ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, session_id);
            ps.setString(2, email);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            long curr = currentTime.getTime();

            Timestamp expired_time = null;
            Timestamp last_used = null;
            long timeout;
            long last = 0;
            timeout = IDMService.TimeOut();
            long expired_t = IDMService.Expired();

            while (rs.next()) {
                expired_time = rs.getTimestamp("expr_time");
                last_used = rs.getTimestamp("last_used");
                //  long expr = expired_time.getTime();
                last = last_used.getTime();
            }
          //  if (curr > expired_t){
            if (currentTime.after(expired_time)) {//|| last_used.getTime() > expired_time.getTime() ){
                expired = true;
                String query1 = "UPDATE session" +
                        " SET status = ?" +
                        " WHERE session_id = ?;";
                PreparedStatement ps1 = IDMService.getCon().prepareStatement(query1);
                ps1.setInt(1, 3);
                ps1.setString(2, session_id);
                ps1.executeUpdate();
                ServiceLogger.LOGGER.info("Table updated. Set status to EXPIRED!");
                return expired;

            }


        expired = false;
        return expired;
        } catch (SQLException e) {

            ServiceLogger.LOGGER.info("Query failed");
        }
        ServiceLogger.LOGGER.info("Not expired.");
        expired = false;
        return expired;


    }

    public static boolean checkRevoked(String email, String session_id) {
        boolean revoked = false;
        try {
            String query = "SELECT session_id, email, status, time_created, last_used, expr_time" +
                    " FROM session" +
                    " WHERE session_id LIKE ? AND email LIKE ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, session_id);
            ps.setString(2, email);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            long curr = currentTime.getTime();

            Timestamp expired_time = null;
            Timestamp last_used = null;
            long timeout;
            long last = 0;
            timeout = IDMService.TimeOut();

            while (rs.next()) {
                expired_time = rs.getTimestamp("expr_time");
                last_used = rs.getTimestamp("last_used");
                //  long expr = expired_time.getTime();
                last = last_used.getTime();
            }


            if (curr - last > timeout) {
                ServiceLogger.LOGGER.info("Session must be revoked");
                revoked = true;
                String query2 = "UPDATE session" +
                        " SET status = ?" +
                        " WHERE session_id = ?;";
                PreparedStatement ps2 = IDMService.getCon().prepareStatement(query2);
                ps2.setInt(1, 4);
                ps2.setString(2, session_id);
                ps2.executeUpdate();
                ServiceLogger.LOGGER.info("Table updated. Set status to REVOKED");
                return revoked;
            } else if (currentTime.getTime() - expired_time.getTime() < timeout) {
                ServiceLogger.LOGGER.info("Session will be revoked");
                revoked = true;
                String query3 = "UPDATE session" +
                        " SET status = ?" +
                        " WHERE session_id = ?;";
                PreparedStatement ps3 = IDMService.getCon().prepareStatement(query3);
                ps3.setInt(1, 4);
                ps3.setString(2, session_id);
                ps3.executeUpdate();
                ServiceLogger.LOGGER.info("Table updated, set status to revoked");
                Session new_session = Session.createSession(email);

                String query4 = "INSERT INTO session (session_id, email, status, time_created, last_used, expr_time) VALUES (?, ?, ?, ?, ? ,? );";
                PreparedStatement ps4 = IDMService.getCon().prepareStatement(query4);
                ps4.setString(1, new_session.getSessionID().toString());
                ps4.setString(2, new_session.getEmail());
                ps4.setInt(3, 1);
                ps4.setTimestamp(4, new_session.getTimeCreated());
                ps4.setTimestamp(5, new_session.getLastUsed());
                ps4.setTimestamp(6, new_session.getExprTime());
                ServiceLogger.LOGGER.info("Generating new session , active");
                ps4.executeUpdate();
                ServiceLogger.LOGGER.info("Successfully added new session");
                return revoked;


            }
            return revoked;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Query failed when trying to set table to REVOKED");
        }

        ServiceLogger.LOGGER.info("Not revoked");
        return revoked;


    }

    public static boolean checkFound(String email, String session_id) {
        boolean found = false;
        try {
            ServiceLogger.LOGGER.info("Discovering if this session can be found");
            String query = "SELECT session_id, email, status, time_created, last_used, expr_time" +
                    " FROM session" +
                    " WHERE session_id LIKE ? AND email LIKE ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, "session_id");
            ps.setString(2, "email");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                found = true;
            }
            return found;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Query did NOT succeed");
        }

        return found;


    }

    public static boolean checkActive(String email, String session_id) {

        boolean active = false;
        boolean found = false;
        try {
            String query = "SELECT session_id, email, status, time_created, last_used, expr_time" +
                    " FROM session" +
                    " WHERE session_id LIKE ? AND email LIKE ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, session_id);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();

            Timestamp last_used = null;
            Timestamp expr_time = null;
            while (rs.next()) {
                found = true;
                last_used = rs.getTimestamp("last_used");
                expr_time = rs.getTimestamp("expr_time");

            }

            if (found) {
                if (last_used.getTime() < expr_time.getTime()) {
                    active = true;
                    String query1 = "UPDATE session" +
                            " SET status = ?" +
                            " WHERE session_id = ? AND email LIKE ?;";
                    PreparedStatement ps1 = IDMService.getCon().prepareStatement(query1);
                    ps1.setInt(1, 1);
                    ps1.setString(2, session_id);
                    ps1.setString(3, email);
                    ServiceLogger.LOGGER.info("About to make session active");
                    ps1.executeUpdate();
                    ServiceLogger.LOGGER.info("Query succeeded: session is now active");
                    return active;


                }


            }


            return active;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("SQL failed");
        }


        return active;

    }

    public static int checkStatus(String email, String session_id) {

        int status_code = 0;
        try {
            String query = "SELECT status" +
                    " FROM session" +
                    " WHERE session_id LIKE ? AND email like ?;";

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, session_id);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                status_code = rs.getInt("status");

            }

        return status_code;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Query failed");
        }
        return status_code;

    }

    public static void updateLastUsed(String email, String session_id){

        try{
            String query = "UPDATE session" +
                            " SET last_used = ?" +
                            " WHERE email = ? AND session_id = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, email);
            ps.setString(3, session_id);
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Last Used time updated");



        }catch (SQLException e){

            ServiceLogger.LOGGER.info("Query failed");
        }




    }
}