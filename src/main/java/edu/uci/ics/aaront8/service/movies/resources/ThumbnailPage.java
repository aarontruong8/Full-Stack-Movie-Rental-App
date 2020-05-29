package edu.uci.ics.aaront8.service.movies.resources;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.configs.IdmConfigs;
import edu.uci.ics.aaront8.service.movies.core.ThumbnailQuery;
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
import java.util.ArrayList;

@Path("thumbnail")
public class ThumbnailPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")
    public Response thumbnail(@Context HttpHeaders headers, String jsonText){


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
        ThumbnailResponseModel tResponseModel = null;
        ThumbnailRequestModel tRequestModel;

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
            ObjectMapper mapper1 = new ObjectMapper();
            tRequestModel = mapper1.readValue(jsonText, ThumbnailRequestModel.class);
            String jsonTex = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("About to use readValue!!!");
            responseModel = mapper.readValue(jsonTex, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO");


        } catch (IOException e){
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
            int resultCode;
            e.printStackTrace();
            resultCode = -1;
            tResponseModel = new ThumbnailResponseModel(resultCode, "Internal server error", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
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
            String [] movie_ids = tRequestModel.getMovie_ids();
            ArrayList<ThumbnailModel> thumbnails = ThumbnailQuery.thumbnail(movie_ids, resultPriv);

            if (thumbnails.size() == 0){
                ServiceLogger.LOGGER.info("No movies found with search parameters.");
                resultCode = 211;
                tResponseModel = new ThumbnailResponseModel(resultCode, "No movies found with search parameters.", thumbnails);
                builder = Response.status(Response.Status.OK).entity(tResponseModel);


            }
            else if (thumbnails.size() > 0){
                ServiceLogger.LOGGER.info("Found movies with search parameters.");
                resultCode = 210;
                tResponseModel = new ThumbnailResponseModel(resultCode, "Found movies with search parameters.", thumbnails);
                builder = Response.status(Response.Status.OK).entity(tResponseModel);




            }

            builder.header("email", email);
            builder.header("session_id", session_id);
             builder.header("transaction_id", transaction_id);

            return builder.build();





        }






    }
}
