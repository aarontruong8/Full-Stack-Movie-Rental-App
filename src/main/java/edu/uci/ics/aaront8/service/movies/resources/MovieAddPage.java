package edu.uci.ics.aaront8.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.j2objc.annotations.ReflectionSupport;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.core.MovieAddQuery;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@SuppressWarnings("Duplicates")
@Path("add")
public class MovieAddPage {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(@Context HttpHeaders headers, String jsonText){

        ServiceLogger.LOGGER.info("Inside movie ADD endpoint");

        String servicePath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort() + MoviesService.getIdmConfigs().getPath();
        String endpointPath = MoviesService.getIdmConfigs().getPrivilegePath();

        PrivRequestModel requestModel;
        ResponseModel responseModel = null;

        MovieAddRequestModel movieAddRequestModel;
        MovieAddResponseModel mResponseModel = null;

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

        requestModel = new PrivRequestModel(email, 3);
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent");
        ServiceLogger.LOGGER.info("Received status " + response.getStatus());

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectMapper mapper1 = new ObjectMapper();
            movieAddRequestModel = mapper.readValue(jsonText, MovieAddRequestModel.class);
            String jsonTex = response.readEntity(String.class);
            responseModel = mapper1.readValue(jsonTex, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped responsed to POJO");

        } catch (IOException e){
            int resultCode;
            if (e instanceof JsonParseException){
                resultCode = -3;
                mResponseModel = new MovieAddResponseModel(resultCode, "JSON parse exception.", null);
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(mResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            } else if (e instanceof JsonMappingException){
                resultCode = -2;
                mResponseModel = new MovieAddResponseModel(resultCode, "JSON mapping exception.", null);
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(mResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            } else {
                resultCode = -1;
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();

            }

        }

        Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(null);
        int resultCode;
        int resultPriv;
        if (responseModel == null) {
            ServiceLogger.LOGGER.info("Bad request ");
            return Response.status(Response.Status.BAD_REQUEST).entity(null).build();

        }

        else {
                resultPriv = responseModel.getResultCode();
            if (resultPriv == 141){
                ServiceLogger.LOGGER.info("Requester has insufficient privilege.");
                resultCode = 215;
                mResponseModel = new MovieAddResponseModel(resultCode, "Could not add movie.", null);
                builder = Response.status(Response.Status.OK).entity(mResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            }

            boolean movieExists = MovieAddQuery.movieExists(movieAddRequestModel.getTitle(), movieAddRequestModel.getDirector());
            if (movieExists){
                resultCode = 216;
                mResponseModel = new MovieAddResponseModel(resultCode, "Movie already exists", null);
                builder = Response.status(Response.Status.OK).entity(mResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            }

            boolean personExists = MovieAddQuery.person_exists(movieAddRequestModel.getDirector());
            int new_person_id = -1;
            if (!personExists){
                new_person_id = MovieAddQuery.add_person(movieAddRequestModel.getDirector());
            }

            String movieAdded = MovieAddQuery.addMovie(movieAddRequestModel.getTitle(), movieAddRequestModel.getYear(), movieAddRequestModel.getDirector(), movieAddRequestModel.getRating(), movieAddRequestModel.getNum_votes(), movieAddRequestModel.getBudget(), movieAddRequestModel.getRevenue(), movieAddRequestModel.getOverview(), movieAddRequestModel.getBackdrop_path(), movieAddRequestModel.getPoster_path(), movieAddRequestModel.isHidden(), new_person_id);
            if (movieAdded != null){
                resultCode = 214;
                mResponseModel = new MovieAddResponseModel(resultCode, "Movie successfully added.", movieAdded);
                builder = Response.status(Response.Status.OK).entity(mResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            }

            resultCode = 215;
            mResponseModel = new MovieAddResponseModel(resultCode, "Could not add movie.", null);
            builder = Response.status(Response.Status.OK).entity(mResponseModel);
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();



        }



    }
}
