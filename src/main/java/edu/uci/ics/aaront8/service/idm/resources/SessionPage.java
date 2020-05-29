package edu.uci.ics.aaront8.service.idm.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.configs.ServiceConfigs;
import edu.uci.ics.aaront8.service.idm.core.RegisterUser;
import edu.uci.ics.aaront8.service.idm.core.checkSession;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.idm.models.LoginResponseModel;
import edu.uci.ics.aaront8.service.idm.models.SessionRequestModel;
import org.glassfish.grizzly.http.util.TimeStamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("session")
//@SuppressWarnings("Duplicates")
public class SessionPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")
    public Response session(@Context HttpHeaders headers, String jsonText){

        SessionRequestModel requestModel;
        LoginResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, SessionRequestModel.class);

        }

        catch (IOException e){
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new LoginResponseModel(resultCode, "JSON parse exception", null);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new LoginResponseModel(resultCode, "JSON mapping exception", null);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                resultCode = -1;
                responseModel = new LoginResponseModel(resultCode, "Internal Server Error", null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();

            }
        }
        int resultCode;
        ServiceLogger.LOGGER.info("Received request for session");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        String email = requestModel.getEmail();
        String session_id = requestModel.getSession_Id();

        if (session_id == null || session_id.equals("") || session_id.length() != 128){
            resultCode = -13;
            responseModel = new LoginResponseModel(resultCode, "Token has invalid length", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.equals("") || email.length() > 50) {
            resultCode = -10;
            responseModel = new LoginResponseModel(resultCode, "Email address has invalid length.", null);
            ServiceLogger.LOGGER.info("Email address has invalid length");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }

        String email_regex = "^[A-Za-z0-9]+@(.+)\\.(.+)$";
        Pattern pattern = Pattern.compile(email_regex);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches() == false) {
            resultCode = -11;
            responseModel = new LoginResponseModel(resultCode, "Email address has invalid format.", null);
            ServiceLogger.LOGGER.info("Email address has invalid format");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }

        boolean emailUsed = RegisterUser.checkEmailInUse(email);
        if (!emailUsed){
            resultCode = 14;
            responseModel = new LoginResponseModel(resultCode, "User not found", null);
            ServiceLogger.LOGGER.info("User not found");
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }
        int status = checkSession.checkStatus(email, session_id);
        boolean checkActive = checkSession.checkActive(email, session_id);

        if (checkActive){
            resultCode = 130;
            responseModel = new LoginResponseModel(resultCode, "Session is active.", session_id);
            ServiceLogger.LOGGER.info("Session is active.");
            return Response.status(Response.Status.OK).entity(responseModel).build();



        }

        if (status == 2){
            resultCode = 132;
            responseModel = new LoginResponseModel(resultCode, "Session is closed", null);
            ServiceLogger.LOGGER.info("Service is closed");
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }
        else if (status == 3){
            resultCode = 131;
            responseModel = new LoginResponseModel(resultCode, "Session is expired", null);
            ServiceLogger.LOGGER.info("Service is expired");
            return Response.status(Response.Status.OK).entity(responseModel).build();


        }
        else if (status == 4){
            resultCode = 133;
            responseModel = new LoginResponseModel(resultCode, "Session is revoked", null);
            ServiceLogger.LOGGER.info("Service is revoked");
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }

        else if (status == 1){

            boolean expired = checkSession.checkExpired(email, session_id);

            if (expired == true){
                resultCode = 131;
                responseModel = new LoginResponseModel(resultCode, "Session is expired", null);
                ServiceLogger.LOGGER.info("Session is expired");
                return Response.status(Response.Status.OK).entity(responseModel).build();

            }



            boolean revoked = checkSession.checkRevoked(email, session_id);
          //  boolean expired = checkSession.checkExpired(email, session_id);

            if (revoked){
                resultCode = 133;
                responseModel = new LoginResponseModel(resultCode, "Session is revoked", null);
                ServiceLogger.LOGGER.info("Session is revoked");
                return Response.status(Response.Status.OK).entity(responseModel).build();


            }


            resultCode = 130;
            responseModel = new LoginResponseModel(resultCode, "Session is active", session_id);
            ServiceLogger.LOGGER.info("Session is active");
            checkSession.updateLastUsed(email, session_id);
            return Response.status(Response.Status.OK).entity(responseModel).build();




        }
        resultCode = 134;
        responseModel = new LoginResponseModel(resultCode, "session not found", null);
        ServiceLogger.LOGGER.info("Session not found");
        return Response.status(Response.Status.OK).entity(responseModel).build();





    }
}
