package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.MovieModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
@SuppressWarnings("Duplicates")
public class PeopleQuery {



    public static boolean people_exist(String name){


        try {
            ServiceLogger.LOGGER.info("Checking query if person exists");
            String person_query = "SELECT p.person_id, p.name" +
                                  " FROM person as p" +
                                  " WHERE p.name LIKE ?;";

            PreparedStatement ps_person = MoviesService.getCon().prepareStatement(person_query);
            ps_person.setString(1, "%" + name + "%");
            ResultSet rs_person = ps_person.executeQuery();

            if (rs_person.next()){
               // person_exists = true;
                ServiceLogger.LOGGER.info("Person exists!");
                return true;
            }


        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Query failed");
        }


        ServiceLogger.LOGGER.info("Person does NOT exist");
        return false;


    }

    public static ArrayList<MovieModel> peopleMovie(String name, Integer limit, Integer offset, String orderby, String direction, int resultPriv){
     //   @SuppressWarnings("Duplicates")
        ArrayList<MovieModel> movies = new ArrayList<MovieModel>();
        ArrayList<Object> all_queries = new ArrayList<Object>();
        all_queries.add(name);

        try {

            String query = "SELECT m.movie_id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden" +
                                     " FROM movie as m" +
                                     " INNER JOIN person as p on m.director_id = p.person_id" +
                                     " INNER JOIN person_in_movie as pm on m.movie_id = pm.movie_id" +
                                     " INNER JOIN person as pp on pm.person_id = pp.person_id" +
                                     " WHERE pp.name LIKE ?";

            if (orderby != null) {
                if (orderby.equals("title") || orderby.equals("rating") || orderby.equals("year")) {
                    query += "\nORDER BY m." + orderby;
                    //         query_num++;
                }
                else
                    query += "\nORDER BY m.title";

            }

            if (orderby == null){
                query += "\nORDER BY m.title";


            }
            if (direction != null) {
                query += " " + direction;
                //    query_num++;
                //  all_queries.add(direction);

            }

            if (direction == null){
                query += " ASC";
                //   query_num++;
                //  all_queries.add("ASC");

            }

            if (limit != null) {
                query += "\nLIMIT ?";
                //       query_num++;
                if (limit == 10 || limit == 25 || limit == 50 || limit == 100)
                    all_queries.add(limit.intValue());
                else
                    all_queries.add(10);

            }

            if (limit == null){
                query += "\nLIMIT 10";
                //   query_num++;
                //    all_queries.add(10);

            }
            //   ServiceLogger.LOGGER.info("Made it past LIMIT checker");
            if (offset != null && limit != null) {
                query += " OFFSET ?";
                //     query_num++;
                if (offset % limit == 0)
                    all_queries.add(offset.intValue());
                else
                    all_queries.add(0);
            }

            if (offset != null && limit == null){
                query += " OFFSET 0";
                //query_num++;


            }

            if (offset == null){
                query += " OFFSET 0";
                // query_num++;
                //   all_queries.add(0);



            }

            query += ";";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            int counter = 1;
            for (Object o : all_queries){

                ps.setObject(counter, o);
                counter++;

            }

            ServiceLogger.LOGGER.info("Trying query:" + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            MovieModel movie = null;
            while(rs.next()){
               // ServiceLogger.LOGGER.info("Hello?");
                ServiceLogger.LOGGER.info(rs.getString("m.movie_id"));
                if (resultPriv == 140)
                    movie = new MovieModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                            rs.getInt("m.year"), rs.getString("p.name"),
                            rs.getFloat("m.rating"), rs.getString("m.backdrop_path"),
                            rs.getString("m.poster_path"), rs.getBoolean("m.hidden"));

                else if (resultPriv == 141)
                    movie = new MovieModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                            rs.getInt("m.year"), rs.getString("p.name"),
                            rs.getFloat("m.rating"), rs.getString("m.backdrop_path"),
                            rs.getString("m.poster_path"), null);

                movies.add(movie);

            }






        } catch (SQLException e){

            ServiceLogger.LOGGER.info("Query failed");
        }

    return movies;



    }


}
