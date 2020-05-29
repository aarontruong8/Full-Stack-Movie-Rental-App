package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateQuery {

    static int p_id = 111111111;


    public static boolean person_exists(String movie_id, String title, int year, String director, float rating, int num_votes, String budget, String revenue, String overview, String backdrop_path, String poster_path, boolean hidden){

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

    public static boolean update(String movie_id, String title, int year, String director, float rating, int num_votes, String budget, String revenue, String overview, String backdrop_path, String poster_path, boolean hidden, int person_id){

        try {
            String query = "UPDATE movie" +
                           " SET title = ?, year = ?, director_id = ?, rating = ?, num_votes = ?, budget = ?, revenue = ?, overview = ?, backdrop_path = ?, poster_path = ?, hidden = ?" +
                           " WHERE movie_id = ?;";
            ServiceLogger.LOGGER.info("Director ID is:" + person_id);
            ServiceLogger.LOGGER.info(title);
            ServiceLogger.LOGGER.info("year: " + year);
            ServiceLogger.LOGGER.info("rating: " +rating);
            ServiceLogger.LOGGER.info("numvotes: " + num_votes);
            ServiceLogger.LOGGER.info("Parsed budget: " + Integer.parseInt(budget));
            ServiceLogger.LOGGER.info("Parsed revenue: " + Integer.parseInt(revenue));
            ServiceLogger.LOGGER.info(overview);
            ServiceLogger.LOGGER.info(backdrop_path);
            ServiceLogger.LOGGER.info(poster_path);
            ServiceLogger.LOGGER.info("hidden: " + hidden);
            ServiceLogger.LOGGER.info(movie_id);







            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, title);
            ps.setInt(2, year);
            ps.setInt(3, person_id);
            ps.setFloat(4, rating);
            ps.setInt(5, num_votes);
            ps.setObject(6, budget);
            ps.setObject(7, revenue);
            ps.setString(8, overview);
            ps.setString(9, backdrop_path);
            ps.setString(10, poster_path);
            ps.setBoolean(11, hidden);
            ps.setString(12, movie_id);
            ps.executeUpdate();
            return true;




        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed");
        }

        return false;

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

    public static boolean exists(String movie_id){


        try {

            String query = "SELECT m.title" +
                            " FROM movie as m" +
                            " WHERE m.movie_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                ServiceLogger.LOGGER.info("Movie exists.");
                return true;
            }


        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Error during SQL query.");
        }

        return false;

    }
}
