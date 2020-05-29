package edu.uci.ics.aaront8.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.core.UpdateQuery;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.PrivRequestModel;
import edu.uci.ics.aaront8.service.movies.models.ResponseModel;
import edu.uci.ics.aaront8.service.movies.models.UpdateRequestModel;
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
@Path("update")
public class UpdatePage {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers, String jsonText){

        ServiceLogger.LOGGER.info("Inside update endpoint");

        String servicePath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort() + MoviesService.getIdmConfigs().getPath();
        String endpointPath = MoviesService.getIdmConfigs().getPrivilegePath();

        PrivRequestModel requestModel;
        ResponseModel responseModel = null;

        UpdateRequestModel updateRequestModel;
        ResponseModel uResponseModel = null;

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
            updateRequestModel = mapper.readValue(jsonText, UpdateRequestModel.class);
            String jsonTex = response.readEntity(String.class);
            responseModel = mapper1.readValue(jsonTex, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped responsed to POJO");

        } catch (IOException e){
            int resultCode;
            if (e instanceof JsonParseException){
                resultCode = -3;
                uResponseModel = new ResponseModel(resultCode, "JSON parse exception.");
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(uResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            } else if (e instanceof JsonMappingException){
                resultCode = -2;
                uResponseModel = new ResponseModel(resultCode, "JSON mapping exception.");
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(uResponseModel);
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
                resultCode = 218;
                uResponseModel = new ResponseModel(resultCode, "Could not update movie.");
                builder = Response.status(Response.Status.OK).entity(uResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            }

         boolean movieExists = UpdateQuery.exists(updateRequestModel.getMovie_id());

            if (!movieExists){
                resultCode = 211;
                uResponseModel = new ResponseModel(resultCode, "No movies found with search parameters.");
                builder = Response.status(Response.Status.OK).entity(uResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            }


            boolean personExists = UpdateQuery.person_exists(updateRequestModel.getMovie_id(), updateRequestModel.getTitle(), updateRequestModel.getYear(), updateRequestModel.getDirector(), updateRequestModel.getRating(), updateRequestModel.getNum_votes(), updateRequestModel.getBudget(), updateRequestModel.getRevenue(), updateRequestModel.getOverview(), updateRequestModel.getBackdrop_path(), updateRequestModel.getPoster_path(), updateRequestModel.isHidden());
            int newPerson_id = -1;

            if (!personExists){ //if person is not in DB, add it to DB
                newPerson_id = UpdateQuery.add_person(updateRequestModel.getDirector());

            }


          boolean updated = UpdateQuery.update(updateRequestModel.getMovie_id(), updateRequestModel.getTitle(), updateRequestModel.getYear(), updateRequestModel.getDirector(), updateRequestModel.getRating(), updateRequestModel.getNum_votes(), updateRequestModel.getBudget(), updateRequestModel.getRevenue(), updateRequestModel.getOverview(), updateRequestModel.getBackdrop_path(), updateRequestModel.getPoster_path(), updateRequestModel.isHidden(), newPerson_id);

            if (updated){
                resultCode = 217;
                ServiceLogger.LOGGER.info("Movie successfully updated.");
                uResponseModel = new ResponseModel(resultCode, "Movie successfully updated.");
                builder = Response.status(Response.Status.OK).entity(uResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            }

            resultCode = 218;
            ServiceLogger.LOGGER.info("Could not update movie.");
            uResponseModel = new ResponseModel(resultCode, "Could not update movie.");
            builder = Response.status(Response.Status.OK).entity(uResponseModel);
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();







        }







    }
}
