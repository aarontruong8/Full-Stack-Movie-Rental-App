package edu.uci.ics.aaront8.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.idm.core.RegisterUser;
import edu.uci.ics.aaront8.service.idm.core.checkPriv;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.idm.models.LoginResponseModel;
import edu.uci.ics.aaront8.service.idm.models.PrivRequestModel;
import edu.uci.ics.aaront8.service.idm.models.RegisterRequestModel;
import edu.uci.ics.aaront8.service.idm.models.RegisterResponseModel;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("privilege")
public class PrivilegePage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")

    public Response privilege(@Context HttpHeaders headers, String jsonText){

        PrivRequestModel requestModel;
        RegisterResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        try{
            requestModel = mapper.readValue(jsonText, PrivRequestModel.class);


        } catch (IOException e){
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new RegisterResponseModel(resultCode, "JSON parse exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new RegisterResponseModel(resultCode, "JSON mapping exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                resultCode = -1;
                responseModel = new RegisterResponseModel(resultCode, "Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();

            }



        }
        int resultCode;
        int plevel = requestModel.getPlevel();

        String email = requestModel.getEmail();
        ServiceLogger.LOGGER.info(email);

        if (plevel < 1 || plevel > 5){
            resultCode = -14;
            responseModel = new RegisterResponseModel(resultCode, "Privilege level out of range");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }

        if (email == null || email.equals("") || email.length() > 50) {
            resultCode = -10;
            responseModel = new RegisterResponseModel(resultCode, "Email address has invalid length.");
            ServiceLogger.LOGGER.info("Email address has invalid length");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }

        String email_regex = "^[A-Za-z0-9]+@(.+)\\.(.+)$";
        Pattern pattern = Pattern.compile(email_regex);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches() == false) {
            resultCode = -11;
            responseModel = new RegisterResponseModel(resultCode, "Email address has invalid format.");
            ServiceLogger.LOGGER.info("Email address has invalid format");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();


        }
        boolean emailUsed = RegisterUser.checkEmailInUse(email);
        if (!emailUsed){
            resultCode = 14;
            responseModel = new RegisterResponseModel(resultCode, "User not found");
            ServiceLogger.LOGGER.info("User not found");
            return Response.status(Response.Status.OK).entity(responseModel).build();


        }
        //check for privilege levels here
        boolean sufficient = checkPriv.priv(email, plevel);
        if (sufficient){
            resultCode = 140;
            responseModel = new RegisterResponseModel(resultCode, "User has sufficient privilege level");
            ServiceLogger.LOGGER.info("User has sufficient privilege level");
            return Response.status(Response.Status.OK).entity(responseModel).build();


        }
        else if (sufficient == false){
            resultCode = 141;
            responseModel = new RegisterResponseModel(resultCode, "User has insufficient privilege level");
            ServiceLogger.LOGGER.info("User has insufficient privilege level");
            return Response.status(Response.Status.OK).entity(responseModel).build();


        }


        return Response.status(Response.Status.OK).entity(null).build();
    }
}
