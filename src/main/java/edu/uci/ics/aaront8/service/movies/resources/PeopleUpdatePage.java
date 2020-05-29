package edu.uci.ics.aaront8.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.movies.MoviesService;
import edu.uci.ics.aaront8.service.movies.core.PeopleUpdateQuery;
import edu.uci.ics.aaront8.service.movies.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.movies.models.PeopleUpdateRequestModel;
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
@Path("people")
public class PeopleUpdatePage {


    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleUpdate(@Context HttpHeaders headers, String jsonText){

        ServiceLogger.LOGGER.info("Inside peopleUpdate endpoint");
        String servicePath = MoviesService.getIdmConfigs().getScheme() + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort() + MoviesService.getIdmConfigs().getPath();
        String endpointPath = MoviesService.getIdmConfigs().getPrivilegePath();

        PrivRequestModel requestModel;
        ResponseModel responseModel = null;

        PeopleUpdateRequestModel peopleUpdateRequestModel;
        ResponseModel pResponseModel = null;

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
            peopleUpdateRequestModel = mapper.readValue(jsonText, PeopleUpdateRequestModel.class);
            String jsonTex = response.readEntity(String.class);
            responseModel = mapper1.readValue(jsonTex, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped responsed to POJO");

        } catch (IOException e){
            int resultCode;
            if (e instanceof JsonParseException){
                resultCode = -3;
                pResponseModel = new ResponseModel(resultCode, "JSON parse exception.");
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(pResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            } else if (e instanceof JsonMappingException){
                resultCode = -2;
                pResponseModel = new ResponseModel(resultCode, "JSON mapping exception.");
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST).entity(pResponseModel);
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
                resultCode = 224;
                pResponseModel = new ResponseModel(resultCode, "Could not update person.");
                builder = Response.status(Response.Status.OK).entity(pResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            }

            boolean personExists = PeopleUpdateQuery.personExists(peopleUpdateRequestModel.getPerson_id());
            if (!personExists){
                resultCode = 223;
                pResponseModel = new ResponseModel(resultCode, "Person does not exist.");
                builder = Response.status(Response.Status.OK).entity(pResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();

            }

            boolean personUpdated = PeopleUpdateQuery.updatePerson(peopleUpdateRequestModel.getPerson_id(), peopleUpdateRequestModel.getName(), peopleUpdateRequestModel.getGender_id(), peopleUpdateRequestModel.getBirthday(), peopleUpdateRequestModel.getDeathday(), peopleUpdateRequestModel.getBiography(), peopleUpdateRequestModel.getBirthplace(), peopleUpdateRequestModel.getPopularity(), peopleUpdateRequestModel.getProfile_path());
            if (personUpdated){
                resultCode = 225;
                pResponseModel = new ResponseModel(resultCode, "People successfully updated.");
                builder = Response.status(Response.Status.OK).entity(pResponseModel);
                builder.header("email", email);
                builder.header("session_id", session_id);
                builder.header("transaction_id", transaction_id);
                return builder.build();


            }

            resultCode = 224;
            pResponseModel = new ResponseModel(resultCode, "Could not update person.");
            builder = Response.status(Response.Status.OK).entity(pResponseModel);
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);
            return builder.build();


        }



    }




}
