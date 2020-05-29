package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@SuppressWarnings("Duplicates")
public class RatingQuery {


    public static boolean movieExists(String movie_id){

        try {

            String query = "SELECT m.title" +
                           " FROM movie as m" +
                           " WHERE m.movie_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                ServiceLogger.LOGGER.info("movie exists!");
                return true;
            }



        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed");
        }
        ServiceLogger.LOGGER.info("Movie does not exist");
        return false;

    }

    public static boolean rate(String movie_id, float rating, int num_votes){

        try  {

            String query = "SELECT m.rating" +
                           " FROM movie as m" +
                           " WHERE m.movie_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ResultSet rs = ps.executeQuery();
            float old_rating = 0;
            if (rs.next()){
                old_rating = rs.getFloat("m.rating");
            }
            float sum = old_rating*num_votes;
            float new_rating = (sum+rating)/(num_votes+1);

            String update_query = "UPDATE movie" +
                                  " SET rating = ?, num_votes = ?" +
                                  " WHERE movie_id = ?;";
            PreparedStatement ps1 = MoviesService.getCon().prepareStatement(update_query);
            ps1.setFloat(1, new_rating);
            ps1.setInt(2, num_votes+1);
            ps1.setString(3, movie_id);
            ps1.executeUpdate();
            ServiceLogger.LOGGER.info("Movie rated!");
            return true;



        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed.");
        }

        ServiceLogger.LOGGER.info("movie could not be rated");

        return false;
    }

    public static int getNumVotes(String movie_id){

        try {

            String query = "SELECT m.num_votes" +
                           " FROM movie as m" +
                           " WHERE m.movie_id = ?;";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt("m.num_votes");
            }



        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed.");
        }

        return -1;


    }
}
