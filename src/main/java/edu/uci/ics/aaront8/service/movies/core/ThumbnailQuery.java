package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.ThumbnailModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ThumbnailQuery {

    public static ArrayList<ThumbnailModel> thumbnail(String [] movie_ids, int resultPriv){



        ArrayList<ThumbnailModel> thumbnails = new ArrayList<>();
        try {

            ServiceLogger.LOGGER.info("These are the movie_ids: " + movie_ids);

            String query = "SELECT m.movie_id, m.title, m.backdrop_path, m.poster_path" +
                           " FROM movie as m" +
                           " WHERE m.movie_id = ?;";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

            for (String id : movie_ids){

                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();
                ThumbnailModel thumbnail;
                while (rs.next()){
                   // thumbnail = new ThumbnailModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                                                 //   rs.getString("m.backdrop_path"), rs.getString("m.poster_path"));

                    if (rs.getString("m.backdrop_path") == null && rs.getString("m.poster_path") == null){
                        thumbnail = new ThumbnailModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                                                    null, null);

                    }
                    else if (rs.getString("m.backdrop_path") == null){
                        thumbnail = new ThumbnailModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                                null, rs.getString("m.poster_path"));


                    }
                    else if (rs.getString("m.poster_path") == null){
                        thumbnail = new ThumbnailModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                                rs.getString("m.backdrop_path"), null);

                    }

                    else {
                        thumbnail = new ThumbnailModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                                rs.getString("m.backdrop_path"), rs.getString("m.poster_path"));


                    }
                    thumbnails.add(thumbnail);


                }




            }


        } catch (SQLException e){

            ServiceLogger.LOGGER.info("Query failed!");
        }




    return thumbnails;



    }


}
