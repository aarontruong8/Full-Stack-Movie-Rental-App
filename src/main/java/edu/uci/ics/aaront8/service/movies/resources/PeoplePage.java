package edu.uci.ics.aaront8.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.configs.IdmConfigs;
import edu.uci.ics.aaront8.service.movies.core.PeopleQuery;
import edu.uci.ics.aaront8.service.movies.core.PeopleSearchQuery;
import edu.uci.ics.aaront8.service.movies.core.PersonGetQuery;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

@Path("people")


public class PeoplePage {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")
    public Response people(@Context HttpHeaders headers, @QueryParam("name") String name, @QueryParam("limit") Integer limit,
                           @QueryParam("offset") Integer offset, @QueryParam("orderby") String orderby, @QueryParam("direction") String direction){


        ServiceLogger.LOGGER.info("TESTING!!!!");
        String servicePath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort() + MoviesService.getIdmConfigs().getPath();

        String endpointPath = MoviesService.getIdmConfigs().getPrivilegePath();

        IdmConfigs idmconfigs = new IdmConfigs();
        String _servicePath = idmconfigs.getScheme() + idmconfigs.getHostName() + idmconfigs.getPort() + idmconfigs.getPath();
        //String endpointPath = "/privilege";
        String _endpointPath = idmconfigs.getPrivilegePath();
        ServiceLogger.LOGGER.info(_servicePath);
        ServiceLogger.LOGGER.info(_endpointPath);
        PrivRequestModel requestModel;
        ResponseModel responseModel = null;
        MovieResponseModel MresponseModel = null;


        //Getting header strings
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");


        //Creating a new client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        //Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Sending request...");

        requestModel = new PrivRequestModel(email, 4);
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent");
        ServiceLogger.LOGGER.info("Received status " + response.getStatus());


        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("About to use readValue!!!");
            responseModel = mapper.readValue(jsonText, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO");



        } catch (IOException e){
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
            int resultCode;
            e.printStackTrace();
            resultCode = -1;
            MresponseModel = new MovieResponseModel(resultCode, "Internal server error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();

        }

        Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(null);
        int resultCode;
        int resultPriv; // determines if user can see HIDDEN OR NOT

        if (responseModel == null) {
            ServiceLogger.LOGGER.info("Bad request ");
            return Response.status(Response.Status.BAD_REQUEST).entity(null).build();

        }

        else {

            resultPriv = responseModel.getResultCode();
            boolean person_exists = PeopleQuery.people_exist(name);

            if (person_exists == false){
                resultCode = 211;
                MresponseModel = new MovieResponseModel(resultCode, "No movies found with search parameters.", null);
                builder = Response.status(Response.Status.OK).entity(MresponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            }

            else if (person_exists){

            ArrayList<MovieModel> movies = PeopleQuery.peopleMovie(name, limit, offset, orderby, direction, resultPriv);
                if (movies.size() == 0){
                    ServiceLogger.LOGGER.info("No movies found with search parameters");
                    resultCode = 211;
                    MresponseModel = new MovieResponseModel(resultCode, "No movies found with search parameters.", null);
                    //return Response.status(Response.Status.OK).entity(MresponseModel).build();
                    builder = Response.status(Response.Status.OK).entity(MresponseModel);
                }

                else if (movies.size() > 0){
                    ServiceLogger.LOGGER.info("Found movie(s) with search parameters.");
                    resultCode = 210;
                    MresponseModel = new MovieResponseModel(resultCode, "Found movie(s) with search parameters.", movies);
                    //    return Response.status(Response.Status.OK).entity(MresponseModel).build();
                    builder = Response.status(Response.Status.OK).entity(MresponseModel);


                }

                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);








            }

       // return builder.build();




        }
        return builder.build();

    }
    @Path("/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleSearch(@Context HttpHeaders headers, @QueryParam("name") String name, @QueryParam("birthday") String birthday,
                                 @QueryParam("movie_title") String movie_title, @QueryParam("limit") Integer limit,
                                 @QueryParam("offset") Integer offset, @QueryParam("orderby") String orderby,
                                 @QueryParam("direction") String direction){


            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            PeopleSearchResponseModel responseModel = null;
            int resultCode;
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(null);
            try {


                ArrayList<PersonSearchModel> people = PeopleSearchQuery.peopleSearchQuery(name, birthday, movie_title, limit, offset, orderby, direction);

                if (people.size() == 0) {
                    resultCode = 213;
                    ServiceLogger.LOGGER.info("No people found with search parameters.");
                    responseModel = new PeopleSearchResponseModel(resultCode, "No people found with search parameters.", null);
                    builder = Response.status(Response.Status.OK).entity(responseModel);


                } else if (people.size() > 0) {
                    resultCode = 212;
                    ServiceLogger.LOGGER.info("Found people with search parameters.");
                    responseModel = new PeopleSearchResponseModel(resultCode, "Found people with search parameters.", people);
                    builder = Response.status(Response.Status.OK).entity(responseModel);


                }


                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);

                return builder.build();
            } catch (Exception e)
            {
                builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null);
                return builder.build();


            }










    }


    @Path("/get/{person_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleGet(@Context HttpHeaders headers, @PathParam("person_id") int person_id){

    //    try {
            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            PersonGetResponseModel responseModel = null;
            int resultCode;
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(null);
            try {
            PersonGetModel person = PersonGetQuery.personGet(person_id);

            if (person == null) {
                resultCode = 213;
                responseModel = new PersonGetResponseModel(resultCode, "No people found with search parameters.", person);
                builder = Response.status(Response.Status.OK).entity(responseModel);


            } else if (person != null) {
                resultCode = 212;
                responseModel = new PersonGetResponseModel(resultCode, "Found people with search parameters.", person);
                builder = Response.status(Response.Status.OK).entity(responseModel);


            }

            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);

            return builder.build();


        } catch (Exception e){
            resultCode = -1;
            responseModel = new PersonGetResponseModel(resultCode, "Internal server error.", null);
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null);
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();
        }







    }







}
