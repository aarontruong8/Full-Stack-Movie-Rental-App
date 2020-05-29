package edu.uci.ics.aaront8.service.movies.resources;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.configs.IdmConfigs;
import edu.uci.ics.aaront8.service.movies.core.GetMovieIdQuery;
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

@Path("get/")
public class GetMovieIdPage {
    @Path("{movie_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")

    public Response getMovieId(@Context HttpHeaders headers, @PathParam("movie_id") String pathMovieId){


        ServiceLogger.LOGGER.info("TESTING GET MOVIE_ID!");
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
        MovieId_MovieResponseModel MresponseModel = null;


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
         //   ServiceLogger.LOGGER.info("About to use readValue!!!");
            responseModel = mapper.readValue(jsonText, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO");


        } catch (IOException e){
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
            int resultCode;
            e.printStackTrace();
            resultCode = -1;
            MresponseModel = new MovieId_MovieResponseModel(resultCode, "Internal server error", null);
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
            GetMovieId_MovieModel movie = GetMovieIdQuery.getMovieId(pathMovieId, resultPriv);

            if (movie == null){
                ServiceLogger.LOGGER.info("No movies found with search parameters.");
                resultCode = 211;
                MresponseModel = new MovieId_MovieResponseModel(resultCode, "No movies found with search parameters.", movie);
                builder = Response.status(Response.Status.OK).entity(MresponseModel);
             //   ServiceLogger.LOGGER.info()

            }

            else {
                ServiceLogger.LOGGER.info("Found movie(s) with search parameters.");
                resultCode = 210;
                MresponseModel = new MovieId_MovieResponseModel(resultCode, "Found movie(s) with search parameters.", movie);
                builder = Response.status(Response.Status.OK).entity(MresponseModel);




            }


            builder.header("email", email);
            builder.header("session_id", session_id);
           builder.header("transaction_id", transaction_id);

            return builder.build();


        }






    }



}
