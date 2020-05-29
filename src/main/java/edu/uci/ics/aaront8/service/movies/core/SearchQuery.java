package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.MovieModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchQuery {


    public static ArrayList<MovieModel> searchQuery(String title, Integer year, String director,
                                                    String genre, Boolean hidden, Integer limit,
                                                    Integer offset, String orderby, String direction, int resultPriv ){
    boolean need_and = false;
    int query_num = 0;
    ArrayList<Object> all_queries = new ArrayList<Object>();

    ArrayList<MovieModel> movies = new ArrayList<MovieModel>();
    try {
     //   ArrayList<MovieModel> movies = new ArrayList<MovieModel>();
        ServiceLogger.LOGGER.info("these are the params: " + title + year + director + genre + hidden + limit + offset + orderby + direction  + resultPriv);
        String query = "SELECT DISTINCT m.movie_id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden" +
                " FROM movie as m" +
                " INNER JOIN person AS p on m.director_id = p.person_id" +
                " INNER JOIN genre_in_movie AS gm on m.movie_id = gm.movie_id" +
                " INNER JOIN genre AS g on gm.genre_id = g.genre_id" +
                " WHERE ";
        if (title != null) {
            query += "m.title LIKE ?";
             need_and = true;
            query_num++;
            all_queries.add("%" + title + "%");
        }

        if (year != null) { // might have to be Integer instead so it could be NULL when user doesnt enter a year
            if (!need_and)
                query += "m.year = ?";
            if (need_and)
            query += " AND m.year = ?";
            query_num++;
            need_and = true;
            all_queries.add(year.intValue());

        }
        if (director != null) {
            if (!need_and)
                query += "p.name LIKE ?";
            if (need_and)
            query += " AND p.name LIKE ?";
            query_num++;
            need_and = true;
            all_queries.add(director);

        }

        if (genre != null) {
            if (!need_and)
                query += "g.name = ?";
            if (need_and)
            query += " AND g.name = ?";
            query_num++;
            need_and = true;
            all_queries.add(genre);


        }
        ServiceLogger.LOGGER.info("made it here!!!!");
        if (hidden != null && resultPriv == 140) {
            if (!need_and)
                query += "(m.hidden = ? OR m.hidden = ?)";
            if (need_and)
            query += " AND (m.hidden = ? OR m.hidden = ?)";
            query_num++;
            need_and = true;
            all_queries.add(hidden.booleanValue());
            all_queries.add(0);

        }

        if (hidden != null && resultPriv == 141){
            if (!need_and)
                query += "m.hidden = ?";
            if (need_and)
                query += " AND m.hidden = ?";
            query_num++;
            need_and = true;
            all_queries.add(0);
        }

        ServiceLogger.LOGGER.info("I passed HIDDEN!!!!");
        if (orderby != null) {
            if (orderby.equals("title") || orderby.equals("rating") || orderby.equals("year")) {
                query += "\nORDER BY m." + orderby;
                query_num++;
            }
            else
                query += "\nORDER BY m.title";
            query_num++;
         //   String new_orderby = "m." + orderby;
        //    all_queries.add(new_orderby);
        }

        if (orderby == null){
            query += "\nORDER BY m.title";
            query_num++;
          //  all_queries.add("m.title");

        }
        if (direction != null) {
            query += " " + direction;
            query_num++;
          //  all_queries.add(direction);

        }

        if (direction == null){
            query += " ASC";
            query_num++;
          //  all_queries.add("ASC");

        }

        if (limit != null) {
            query += "\nLIMIT ?";
            query_num++;
            if (limit == 10 || limit == 25 || limit == 50 || limit == 100)
                all_queries.add(limit.intValue());
            else
                all_queries.add(10);

        }

        if (limit == null){
            query += "\nLIMIT 10";
            query_num++;
            //    all_queries.add(10);

        }
     //   ServiceLogger.LOGGER.info("Made it past LIMIT checker");
        if (offset != null && limit != null) {
            query += " OFFSET ?";
            query_num++;
            if (offset % limit == 0)
                all_queries.add(offset.intValue());
            else
                all_queries.add(0);
        }

        if (offset != null && limit == null){
            query += " OFFSET 0";
            query_num++;


        }

        if (offset == null){
            query += " OFFSET 0";
            query_num++;
            //   all_queries.add(0);



        }
     //   ServiceLogger.LOGGER.info("made it past OFFSET CHECKER!");
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
           // ServiceLogger.LOGGER.info("Movie title" + movie.getTitle());
            movies.add(movie);

        }



    } catch (SQLException e){
        ServiceLogger.LOGGER.info("Query failed.");

    }
    return movies;

    }
}
