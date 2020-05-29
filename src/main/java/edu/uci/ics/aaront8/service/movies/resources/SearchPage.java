package edu.uci.ics.aaront8.service.movies.resources;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.configs.IdmConfigs;
import edu.uci.ics.aaront8.service.movies.core.SearchQuery;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.MovieModel;
import edu.uci.ics.aaront8.service.movies.models.MovieResponseModel;
import edu.uci.ics.aaront8.service.movies.models.PrivRequestModel;
import edu.uci.ics.aaront8.service.movies.models.ResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.management.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers, @QueryParam("title") String title,
                            @QueryParam("year") Integer year, @QueryParam("director") String director,
                           @QueryParam("genre") String genre, @QueryParam("hidden") Boolean hidden,
                           @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                           @QueryParam("orderby") String orderby, @QueryParam("direction") String direction){

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


    } catch (IOException e) {
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
        ArrayList<MovieModel> movies = SearchQuery.searchQuery(title, year, director, genre, hidden, limit, offset, orderby, direction, resultPriv);
       // Response.ResponseBuilder builder;


        if (movies.size() == 0){
            ServiceLogger.LOGGER.info("No movies found with search parameters");
            resultCode = 211;
            MresponseModel = new MovieResponseModel(resultCode, "No movies found with search parameters", null);
            //return Response.status(Response.Status.OK).entity(MresponseModel).build();
            builder = Response.status(Response.Status.OK).entity(MresponseModel);
        }

        else if (movies.size() > 0){
            ServiceLogger.LOGGER.info("Found movie(s) with search parameters.");
            resultCode = 210;
         //   ServiceLogger.LOGGER.info(movies.get(0).getTitle());
            ServiceLogger.LOGGER.info("Size: " + movies.size());
            MresponseModel = new MovieResponseModel(resultCode, "Found movie(s) with search parameters", movies);
        //    return Response.status(Response.Status.OK).entity(MresponseModel).build();
            builder = Response.status(Response.Status.OK).entity(MresponseModel);


        }

        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);



        return builder.build();


    }






 //   return Response.status(Response.Status.OK).entity(null).build();


    }









}
