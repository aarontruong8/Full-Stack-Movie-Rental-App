package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;

import java.sql.Date;
//import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleUpdateQuery {


    public static boolean personExists(int person_id){

        try{

            String query = "SELECT p.name" +
                            " FROM person as p" +
                            " WHERE p.person_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1, person_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return true;
            }

        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed");
        }

        return false;
    }


    public static boolean updatePerson(int person_id, String name, int gender_id, Date birthday, Date deathday, String biography, String birthplace, float popularity, String profile_path){

        try {
            String query = "UPDATE person" +
                           " SET name = ?, gender_id = ?, birthday = ?, deathday = ?, biography = ?, birthplace = ?, popularity = ?, profile_path = ?" +
                            " WHERE person_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
           // Date date = new Date();
            //java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            Date current_date = new Date(System.currentTimeMillis());

            if (birthday.after(current_date))
                birthday = null;
            if (deathday.after(current_date))
                deathday = null;

            ServiceLogger.LOGGER.info("id: " + person_id);
            ServiceLogger.LOGGER.info(name);
            ServiceLogger.LOGGER.info("gen id: " + gender_id);
            ServiceLogger.LOGGER.info("birthday:" + birthday);
            ServiceLogger.LOGGER.info("deathday:" + deathday);
            ServiceLogger.LOGGER.info(biography);
            ServiceLogger.LOGGER.info(birthplace);
            ServiceLogger.LOGGER.info("pop:" + popularity);
            ServiceLogger.LOGGER.info(profile_path);


            ps.setString(1, name);
            ps.setInt(2, gender_id);
            ps.setDate(3, birthday);
            ps.setDate(4, deathday);
            ps.setString(5, biography);
            ps.setString(6, birthplace);
            ps.setFloat(7, popularity);
            ps.setString(8, profile_path);
            ps.setInt(9, person_id);
            ps.executeUpdate();
            return true;










        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed.");
        }


        return false;

    }
}
