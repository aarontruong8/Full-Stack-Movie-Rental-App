package edu.uci.ics.aaront8.service.movies.core;

import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.PersonSearchModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
@SuppressWarnings("Duplicates")
public class PeopleSearchQuery {

    public static ArrayList<PersonSearchModel> peopleSearchQuery(String name, String birthday, String movie_title,
                                                                 Integer limit, Integer offset, String orderby,
                                                                 String direction){


        boolean need_and = false;
        ArrayList<Object> all_queries = new ArrayList<Object>();
        ArrayList<PersonSearchModel> people = new ArrayList<PersonSearchModel>();

        try{


            String query = "SELECT DISTINCT p.person_id, p.name, p.birthday, p.popularity, p.profile_path" +
                           " FROM person as p" +
                           " INNER JOIN person_in_movie as pm on p.person_id = pm.person_id" +
                           " INNER JOIN movie as m on pm.movie_id = m.movie_id" +
                           " WHERE ";

            if (name != null){
                query += "p.name LIKE ?";
                need_and = true;
                all_queries.add("%" + name + "%");


            }

            if (birthday != null){
                if (!need_and)
                    query += "p.birthday = ?";
                if (need_and)
                    query += " AND p.birthday = ?";
                need_and = true;
                all_queries.add(birthday);

            }

            if (movie_title != null){
                if (!need_and)
                    query += "m.title LIKE ?";
                if (need_and)
                    query += " AND m.title LIKE ?";
                need_and = true;
                all_queries.add("%" + movie_title + "%");

            }

            if (orderby != null) {
                if (orderby.equals("name") || orderby.equals("birthday") || orderby.equals("popularity")) {
                    query += "\nORDER BY p." + orderby;
                }
                else
                    query += "\nORDER BY p.name";
                //   String new_orderby = "m." + orderby;
                //    all_queries.add(new_orderby);
            }

            if (orderby == null){
                query += "\nORDER BY p.name";
                //  all_queries.add("m.title");

            }
            if (direction != null) {
                query += " " + direction;
                //  all_queries.add(direction);

            }

            if (direction == null){
                query += " ASC";
                //  all_queries.add("ASC");

            }

            if (limit != null) {
                query += "\nLIMIT ?";
                if (limit == 10 || limit == 25 || limit == 50 || limit == 100)
                    all_queries.add(limit.intValue());
                else
                    all_queries.add(10);

            }

            if (limit == null){
                query += "\nLIMIT 10";
                //    all_queries.add(10);

            }
            //   ServiceLogger.LOGGER.info("Made it past LIMIT checker");
            if (offset != null && limit != null) {
                query += " OFFSET ?";
                if (offset % limit == 0)
                    all_queries.add(offset.intValue());
                else
                    all_queries.add(0);
            }

            if (offset != null && limit == null){
                query += " OFFSET 0";


            }

            if (offset == null){
                query += " OFFSET 0";
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

            PersonSearchModel person = null;
            int num = 0;
            while (rs.next()){
                num++;
                if (rs.getDate("p.birthday") == null && rs.getString("p.profile_path") == null)
                    person = new PersonSearchModel(rs.getInt("p.person_id"), rs.getString("p.name"),
                            null, rs.getFloat("p.popularity"), null);

                else if (rs.getString("p.profile_path") == null)
                    person = new PersonSearchModel(rs.getInt("p.person_id"), rs.getString("p.name"),
                            rs.getDate("p.birthday").toString(), rs.getFloat("p.popularity"), null);

                else if (rs.getDate("p.birthday") == null)
                    person = new PersonSearchModel(rs.getInt("p.person_id"), rs.getString("p.name"),
                            null, rs.getFloat("p.popularity"), rs.getString("p.profile_path"));
                else
                person = new PersonSearchModel(rs.getInt("p.person_id"), rs.getString("p.name"),
                        rs.getDate("p.birthday").toString(), rs.getFloat("p.popularity"), rs.getString("p.profile_path"));

/*
                person.setPerson_id(rs.getInt("p.person_id"));
                person.setName(rs.getString("p.name"));
                if (rs.getDate("p.birthday") != null)
                    person.setBirthday(rs.getDate("p.birthday").toString());
                if (rs.getString("p.profile_path") != null)
                    person.setProfile_path(rs.getString("p.profile_path"));
                person.setPopularity(rs.getFloat("p.popularity"));
              */
                people.add(person);
                ServiceLogger.LOGGER.info("Number of people : " + num);
                ServiceLogger.LOGGER.info("Successfully added :" + person.getName());

            }






        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Query failed");

        }



    return people;



    }


}
