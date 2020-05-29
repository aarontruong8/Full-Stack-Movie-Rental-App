package edu.uci.ics.aaront8.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.core.RegisterUser;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.idm.models.RegisterRequestModel;
import edu.uci.ics.aaront8.service.idm.models.RegisterResponseModel;
import edu.uci.ics.aaront8.service.idm.security.Crypto;
import org.apache.commons.codec.binary.Hex;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("register")
public class RegisterPage {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response register(@Context HttpHeaders headers, String jsonText) {

        RegisterRequestModel requestModel;
        RegisterResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);

        } catch (IOException e) {
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

        ServiceLogger.LOGGER.info("Received request to register, salt and hash password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        int resultCode;
        String email = requestModel.getEmail();
        char[] password = requestModel.getPassword();
      //  String pword = password.toString();

        if (email == "" || email == null || email.length() > 50) {
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
        //case -12
        if (password == null || password.length == 0) {
            resultCode = -12;
            responseModel = new RegisterResponseModel(resultCode, "Password has invalid length");
            ServiceLogger.LOGGER.info("Password has invalid length");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }
        //case 12
        if (password.length < 7 || password.length > 16) {
            resultCode = 12;
            responseModel = new RegisterResponseModel(resultCode, "Password does not meet length requirements");
            ServiceLogger.LOGGER.info("Password does not meet length requirements");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

/*
        String password_regex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+";

        Pattern pattern1 = Pattern.compile(password_regex);
        String pword = password.toString();
        Matcher matcher1 = pattern1.matcher(pword);
        if (matcher1.matches() == false) {
            resultCode = 13;
            responseModel = new RegisterResponseModel(resultCode, "Password does not meet character requirements");
            ServiceLogger.LOGGER.info("Password does not meet character requirements");
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }
        */

        if (!checkUpperCase(password) || !checkLowerCase(password) || !checkDigit(password)){
            resultCode = 13;
            responseModel = new RegisterResponseModel(resultCode, "Password does not meet character requirements");
            ServiceLogger.LOGGER.info("Password does not meet character requirements");
            return Response.status(Response.Status.OK).entity(responseModel).build();



        }
    //    ServiceLogger.LOGGER.info("ABOUT TO CHECK IF EMAIL EXISTS!!!!!");
        boolean emailUsed = RegisterUser.checkEmailInUse(email);
      //  ServiceLogger.LOGGER.info("JUST CHECKED!!!!");
        if (emailUsed == true) {
            resultCode = 16;
            responseModel = new RegisterResponseModel(resultCode, "Email already in use.");
      //      ServiceLogger.LOGGER.info("email already in use");
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }


        // User registered successfully . case 110
        byte[] salt = Crypto.genSalt();

        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

        String encodedSalt = Hex.encodeHexString(salt);
        String encodedPassword = Hex.encodeHexString(hashedPassword);

       // String query = "INSERT INTO user (email, status, plevel, salt, pword) VALUES (" +
            //    email + "," + 1 + "," + 5 + "," + encodedSalt + "," + encodedPassword + ");";
      //  ServiceLogger.LOGGER.info("Trying query: " + p)
        String query = "INSERT INTO user (email, status, plevel, salt, pword) VALUES (?, ?, ?, ?, ?);";
        try {
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setInt(2, 1);
            ps.setInt(3, 5);
            ps.setString(4, encodedSalt);
            ps.setString(5, encodedPassword);
            ServiceLogger.LOGGER.info("Trying query" + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query is doing fine");

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed!!!");
            e.printStackTrace();
        }

            resultCode = 110;
            responseModel = new RegisterResponseModel(resultCode, "User registered successfully.");
            ServiceLogger.LOGGER.info("User registered successfully");
            return Response.status(Response.Status.OK).entity(responseModel).build();

     //   return Response.status(Response.Status.OK).entity(null).build();
    }



    public boolean checkUpperCase(char [] password){
        boolean hasUpperCase = false;
        for (int i = 0; i < password.length; ++i){

            if (Character.isUpperCase(password[i]))
                hasUpperCase = true;

        }

        return hasUpperCase;

    }

    public boolean checkLowerCase(char [] password){
        boolean hasLowerCase = false;
        for (char c: password)
            if (Character.isLowerCase(c))
                hasLowerCase = true;


        return hasLowerCase;
    }

    public boolean checkDigit(char [] password){
        boolean hasDigit = false;
        for (char c: password)
            if (Character.isDigit(c))
                hasDigit = true;



        return hasDigit;
    }

}
