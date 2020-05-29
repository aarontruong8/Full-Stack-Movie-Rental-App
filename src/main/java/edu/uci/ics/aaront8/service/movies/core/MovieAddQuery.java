package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@SuppressWarnings("Duplicates")
public class MovieAddQuery {

    static int m_id = 5;
    static int p_id = 77777779;

    public static boolean movieExists(String title, String director){

        try {

            String query = "SELECT m.title" +
                           " FROM movie as m" +
                           " INNER JOIN person as p on m.director_id = p.person_id" +
                           " WHERE m.title = ? AND p.name = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, director);

            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                ServiceLogger.LOGGER.info("This movie exists");
                return true;
            }


        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed.");
        }
        ServiceLogger.LOGGER.info("Movie not found");
        return false;

    }

    public static String addMovie(String title, int year, String director, float rating, int num_votes, String budget, String revenue, String overview, String backdrop_path, String poster_path, boolean hidden, int person_id){

        try {

            String query = "INSERT INTO movie (movie_id, title, year, director_id, rating, num_votes, budget, revenue, overview, backdrop_path, poster_path, hidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            String new_title = "cs000000" + m_id + title;
            String movie_id = "cs000000" + m_id;
            m_id++;
            ServiceLogger.LOGGER.info(new_title);
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ps.setString(2, new_title);
            ps.setInt(3, year);
            ps.setInt(4, person_id);
            ps.setFloat(5, rating);
            ps.setInt(6, num_votes);
            ps.setObject(7, budget);
            ps.setObject(8, revenue);
            ps.setString(9, overview);
            ps.setString(10, backdrop_path);
            ps.setString(11, poster_path);
            ps.setBoolean(12, hidden);
            ps.executeUpdate();
            return movie_id;



        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed");
        }


        return null;


    }

    public static int add_person(String director){

        try {

            String query = "INSERT INTO person (person_id, name) VALUES (?, ?);";

            String query1 = "SELECT p.person_id" +
                    " FROM person as p" +
                    " WHERE p.name = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            PreparedStatement ps1 = MoviesService.getCon().prepareStatement(query1);
            ps.setInt(1, p_id);
            p_id++;
            ps.setString(2, director);
            ServiceLogger.LOGGER.info("About to execute update");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Added new person");

            ps1.setString(1, director);
            ResultSet rs = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Trying to find person_id of new person");
            int ans = -1;
            if (rs.next()){
                ans = rs.getInt("p.person_id");

            }
            ServiceLogger.LOGGER.info("Found person_id : " + ans);
            return ans;



        } catch (SQLException e){
            ServiceLogger.LOGGER.info("SQL Error");

        }

        return -1;
    }

    public static boolean person_exists(String director){

        try {

            String person_query = "SELECT p.person_id" +
                    " FROM person as p" +
                    " WHERE p.name = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(person_query);
            ps.setString(1, director);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                ServiceLogger.LOGGER.info("Person exists in database.");
                return true;
            }


        } catch (SQLException e){
            ServiceLogger.LOGGER.info("SQL ERROR");
        }
        ServiceLogger.LOGGER.info("Person not in database");
        return false;


    }
}
