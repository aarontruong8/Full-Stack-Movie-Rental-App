package edu.uci.ics.aaront8.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.core.RatingQuery;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.PrivRequestModel;
import edu.uci.ics.aaront8.service.movies.models.RatingRequestModel;
import edu.uci.ics.aaront8.service.movies.models.ResponseModel;
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

@Path("rating")
public class MovieRatingPage {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rating(@Context HttpHeaders headers, String jsonText){


        ServiceLogger.LOGGER.info("Inside rating endpoint");

        String servicePath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort() + MoviesService.getIdmConfigs().getPath();
        String endpointPath = MoviesService.getIdmConfigs().getPrivilegePath();

        PrivRequestModel requestModel;
        ResponseModel responseModel = null;

        RatingRequestModel ratingRequestModel;
        ResponseModel rResponseModel = null;

        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        try {
            ObjectMapper mapper = new ObjectMapper();
            ratingRequestModel = mapper.readValue(jsonText, RatingRequestModel.class);
            ServiceLogger.LOGGER.info("Succesfully mapped response to POJO.");

        } catch (IOException e){
            int resultCode;
            if (e instanceof JsonParseException){
                resultCode = -3;
                rResponseModel = new ResponseModel(resultCode, "JSON parse exception.");
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(rResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            } else if (e instanceof JsonMappingException){
                resultCode = -2;
                rResponseModel = new ResponseModel(resultCode, "JSON mapping exception.");
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(rResponseModel);
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

        boolean movieExists = RatingQuery.movieExists(ratingRequestModel.getMovie_id());
        if (!movieExists){
            resultCode = 211;
            rResponseModel = new ResponseModel(resultCode, "No movies found with search parameters.");
            builder = Response.status(Response.Status.OK).entity(rResponseModel);
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();

        }
        int numVotes = RatingQuery.getNumVotes(ratingRequestModel.getMovie_id());
        boolean movieRated = RatingQuery.rate(ratingRequestModel.getMovie_id(), ratingRequestModel.getRating(), numVotes);

        if (movieRated){
            resultCode = 250;
            rResponseModel = new ResponseModel(resultCode, "Rating successfully updated.");
            builder = Response.status(Response.Status.OK).entity(rResponseModel);
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();


        }
        resultCode = 251;
        rResponseModel = new ResponseModel(resultCode, "Could not update rating.");
        builder = Response.status(Response.Status.OK).entity(rResponseModel);
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);
        return builder.build();

    }

}
