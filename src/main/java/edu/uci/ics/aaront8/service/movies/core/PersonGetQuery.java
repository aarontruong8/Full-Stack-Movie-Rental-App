package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.PersonGetModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonGetQuery {



    public static PersonGetModel personGet(int person_id){
        PersonGetModel person = null;
        try {

            String query = "SELECT p.person_id, p.name, g.gender_name, p.birthday, p.deathday, p.biography, p.birthplace, p.popularity, p.profile_path" +
                           " FROM person as p" +
                           " INNER JOIN gender as g on p.gender_id = g.gender_id" +
                           " WHERE p.person_id = ?;";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1, person_id);

           // PersonGetModel person = null;
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query good!");

            if (rs.next()){
                ServiceLogger.LOGGER.info("Person id:" + rs.getString("p.person_id"));
               if (rs.getDate("p.deathday") == null) {
                   person = new PersonGetModel(rs.getInt("p.person_id"), rs.getString("p.name"),
                           rs.getString("g.gender_name"), rs.getDate("p.birthday").toString(),
                           null, rs.getString("p.biography"),
                           rs.getString("p.birthplace"), rs.getFloat("p.popularity"), rs.getString("p.profile_path"));
                   // ServiceLogger.LOGGER.info("deathday is null");
               }
                else
                person = new PersonGetModel(rs.getInt("p.person_id"), rs.getString("p.name"),
                        rs.getString("g.gender_name"), rs.getDate("p.birthday").toString(),
                        rs.getDate("p.deathday").toString(), rs.getString("p.biography"),
                        rs.getString("p.birthplace"), rs.getFloat("p.popularity"), rs.getString("p.profile_path"));



            }






        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed");
        }




    return person;



    }





}
