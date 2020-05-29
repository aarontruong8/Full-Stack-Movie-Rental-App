package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@SuppressWarnings("Duplicates")
public class PeopleAddQuery {


    public static boolean checkPersonID(int person_id){

        try {

            String query = "SELECT p.name" +
                           " FROM person as p" +
                           " WHERE p.person_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1, person_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                ServiceLogger.LOGGER.info("Found! Person ID already exists");
                return true;

            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("SQL ERROR");

        }
        ServiceLogger.LOGGER.info("Person ID does not currently exist");
        return false;
    }

    public static boolean addPerson(int person_id, String name, int gender_id, Date birthday, Date deathday, String biography, String birthplace, float popularity, String profile_path){
        try {
            String query = "INSERT INTO person (person_id, name, gender_id, birthday, deathday, biography, birthplace, popularity, profile_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            Date current_date = new Date(System.currentTimeMillis());
            if (birthday.after(current_date))
                birthday = null;
            if (deathday.after(current_date))
                deathday = null;
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1, person_id);
            ps.setString(2, name);
            ps.setInt(3, gender_id);
            ps.setDate(4, birthday);
            ps.setDate(5, deathday);
            ps.setString(6, biography);
            ps.setString(7, birthplace);
            ps.setFloat(8, popularity);
            ps.setString(9, profile_path);
            ps.executeUpdate();
            return true;



        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed.");
        }

        return false;




    }
}
