package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.MovieModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
@SuppressWarnings("Duplicates")
public class PhraseQuery {



    public static ArrayList<MovieModel> phraseQuery(String phraseString, Integer limit, Integer offset, String orderby, String direction, int resultPriv){


    boolean need_and = false;
    ArrayList<Object> all_queries = new ArrayList<Object>();
    ArrayList<MovieModel> movies = new ArrayList<MovieModel>();

    try {

        ServiceLogger.LOGGER.info("These are the params :" + phraseString + limit + offset + orderby + direction);
        ArrayList<String> phraseList = new ArrayList<String>(Arrays.asList(phraseString.split(",")));
        ServiceLogger.LOGGER.info("These are all the phrases: ");
        for (String s : phraseList)
            ServiceLogger.LOGGER.info(s);



        String query = "SELECT DISTINCT m.movie_id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden" +
                       " FROM movie as m" +
                       " INNER JOIN person AS p on m.director_id = p.person_id" +
                       " INNER JOIN keyword_in_movie AS km on m.movie_id = km.movie_id" +
                       " INNER JOIN keyword AS k on km.keyword_id = k.keyword_id" +
                       " WHERE k.name IN (";

        for (int index = 0; index < phraseList.size(); index++){

            if (index != phraseList.size() - 1) {
                query += "?,";
                all_queries.add(phraseList.get(index));
            }
            else if (index == phraseList.size() - 1) {
                query += "?";
                all_queries.add(phraseList.get(index));
            }


        }
        query += ")";
        query += "\nGROUP BY m.movie_id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden";
        query += "\nHAVING COUNT(*) = ?";
        all_queries.add(phraseList.size());

     //   ServiceLogger.LOGGER.info("I passed HIDDEN!!!!");
        if (orderby != null) {
            if (orderby.equals("title") || orderby.equals("rating") || orderby.equals("year")) {
                query += "\nORDER BY m." + orderby;
       //         query_num++;
            }
            else
                query += "\nORDER BY m.title";
        //    query_num++;
            //   String new_orderby = "m." + orderby;
            //    all_queries.add(new_orderby);
        }

        if (orderby == null){
            query += "\nORDER BY m.title";
        //    query_num++;
            //  all_queries.add("m.title");

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
            ServiceLogger.LOGGER.info("Hello?");
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
           // ServiceLogger.LOGGER.info(movie.getTitle());
            movies.add(movie);

        }


    } catch (SQLException e){
        ServiceLogger.LOGGER.info("Query failed.");
    }





    return movies;

    }




}
