package edu.uci.ics.aaront8.service.idm.core;

import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;

import javax.xml.ws.Service;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class checkPriv {


    public static boolean priv(String email, int plevel){

    boolean sufficient = false;

    try {

        ServiceLogger.LOGGER.info("Checking if provided User has sufficient privilege level");
        String query = "SELECT plevel" +
                        " FROM user" +
                        " WHERE email LIKE ?;";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);

        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded.");
        int stored_plevel = -1;
        while(rs.next()){
            stored_plevel = rs.getInt("plevel");

        }
        if (stored_plevel != -1 && stored_plevel <= plevel) {
            sufficient = true;
            return sufficient;
        }





    } catch (SQLException e){
        ServiceLogger.LOGGER.info("Query failed");

    }


    return sufficient;

    }
}
