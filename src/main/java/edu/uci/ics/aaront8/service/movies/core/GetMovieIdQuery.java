package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.GenreModel;
import edu.uci.ics.aaront8.service.movies.models.GetMovieId_MovieModel;
import edu.uci.ics.aaront8.service.movies.models.MovieModel;
import edu.uci.ics.aaront8.service.movies.models.PersonModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetMovieIdQuery {



    public static GetMovieId_MovieModel getMovieId(String pathMovieId, int resultPriv){


       // ArrayList<Object> all_queries = new ArrayList<Object>();
      //  GetMovieId_MovieModel movie = new GetMovieId_MovieModel();
        GetMovieId_MovieModel movie = null;
        GenreModel genre = null;
        PersonModel person = null;
        ArrayList<GenreModel> genres = new ArrayList<GenreModel>();
        ArrayList<PersonModel> people = new ArrayList<PersonModel>();

        try {
            ServiceLogger.LOGGER.info("This is the path param: " + pathMovieId);
            String query = "SELECT DISTINCT m.movie_id, m.title, m.year, p.name, m.rating, m.num_votes, m.budget, m.revenue, m.overview, m.backdrop_path, m.poster_path, m.hidden" +
                           " FROM movie as m" +
                           " INNER JOIN person as p on m.director_id = p.person_id" +
                           " WHERE m.movie_id = ?;";

            String genreQuery = "SELECT DISTINCT g.genre_id, g.name" +
                                " FROM genre_in_movie as gm" +
                                " INNER JOIN genre as g on gm.genre_id = g.genre_id" +
                                " INNER JOIN movie as m on gm.movie_id = m.movie_id" +
                                " WHERE m.movie_id = ?;";

            String peopleQuery = "SELECT DISTINCT p.person_id, p.name" +
                                 " FROM person as p" +
                                 " INNER JOIN person_in_movie as pe on p.person_id = pe.person_id" +
                                 " INNER JOIN movie as m on pe.movie_id = m.movie_id" +
                                 " WHERE m.movie_id = ?;";




            PreparedStatement psGenre = MoviesService.getCon().prepareStatement(genreQuery);
            psGenre.setString(1, pathMovieId);
            ServiceLogger.LOGGER.info("About to execute genre query: " + psGenre.toString());
            ResultSet rsGenre = psGenre.executeQuery();
            ServiceLogger.LOGGER.info("Genre query success");
            PreparedStatement psPeople = MoviesService.getCon().prepareStatement(peopleQuery);
            psPeople.setString(1, pathMovieId);
            ServiceLogger.LOGGER.info("About to execute people query: " + psPeople.toString());
            ResultSet rsPeople = psPeople.executeQuery();
            ServiceLogger.LOGGER.info("People query success");




            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, pathMovieId);
            ServiceLogger.LOGGER.info("About to execute query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

        //    GetMovieId_MovieModel movie = null;
        //    GenreModel genre = null;
         //   PersonModel person = null;
        //    ArrayList<GenreModel> genres = new ArrayList<GenreModel>();
         //   ArrayList<PersonModel> people = new ArrayList<PersonModel>();

        while (rsGenre.next()){
            genre = new GenreModel(rsGenre.getInt("g.genre_id"), rsGenre.getString("g.name"));
            genres.add(genre);


        }

        while (rsPeople.next()){

            person = new PersonModel(rsPeople.getInt("p.person_id"), rsPeople.getString("p.name"));
            people.add(person);

        }


    //    ServiceLogger.LOGGER.info("TESTING");
           while (rs.next()){
            //    ServiceLogger.LOGGER.info("Testing!!");
                if (resultPriv == 140){
              //      genre = new GenreModel(rs.getInt("g.genre_id"), rs.getString("g.name"));
               //     person = new PersonModel(rs.getInt("pe.person_id"), rs.getString("pe.name"));
                    movie = new GetMovieId_MovieModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                            rs.getInt("m.year"), rs.getString("p.name"),
                            rs.getFloat("m.rating"), rs.getInt("m.num_votes"), rs.getObject("m.budget").toString(), rs.getObject("m.revenue").toString(), rs.getString("m.overview"), rs.getString("m.backdrop_path"),
                            rs.getString("m.poster_path"), rs.getBoolean("m.hidden"), genres, people);



                }
                else if (resultPriv == 141){
                    movie = new GetMovieId_MovieModel(rs.getString("m.movie_id"), rs.getString("m.title"),
                            rs.getInt("m.year"), rs.getString("p.name"),
                            rs.getFloat("m.rating"), rs.getInt("m.num_votes"), rs.getObject("m.budget").toString(), rs.getObject("m.revenue").toString(), rs.getString("m.overview"), rs.getString("m.backdrop_path"),
                            rs.getString("m.poster_path"), null, genres, people);


                }


            }

            if (movie == null)
                return movie;

            ServiceLogger.LOGGER.info("Movie: " + movie.getMovie_id() + movie.getTitle() + movie.getYear() + movie.getDirector() +
                    movie.getRating() + movie.getNum_votes() + movie.getBudget() + movie.getRevenue() + movie.getOverview() + movie.getPoster_path() +
                    movie.getBackdrop_path() + movie.getHidden());
            ServiceLogger.LOGGER.info("genre size: " + movie.getGenres().size());
            ServiceLogger.LOGGER.info("People size : " + movie.getPeople().size());

        } catch (SQLException e){

            ServiceLogger.LOGGER.info("Query failed!");
        }

    ServiceLogger.LOGGER.info("About to return now");
    return movie;

    }


}
