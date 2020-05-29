package edu.uci.ics.aaront8.service.idm.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.aaront8.service.idm.IDMService;
import edu.uci.ics.aaront8.service.idm.core.LoginUser;
import edu.uci.ics.aaront8.service.idm.core.RegisterUser;
import edu.uci.ics.aaront8.service.idm.logger.ServiceLogger;
import edu.uci.ics.aaront8.service.idm.models.LoginResponseModel;
import edu.uci.ics.aaront8.service.idm.models.RegisterRequestModel;
import edu.uci.ics.aaront8.service.idm.models.RegisterResponseModel;
import edu.uci.ics.aaront8.service.idm.security.Crypto;
import edu.uci.ics.aaront8.service.idm.security.Session;
import edu.uci.ics.aaront8.service.idm.security.Token;
import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.glassfish.grizzly.http.util.TimeStamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Service;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("login")
public class LoginPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")
    public Response login(@Context HttpHeaders headers, String jsonText){

        RegisterRequestModel requestModel;
        LoginResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);


        } catch (IOException e){
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

        ServiceLogger.LOGGER.info("Received request to login");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        int resultCode;
        String email = requestModel.getEmail();
        char[] password = requestModel.getPassword();

        if (password == null || password.length == 0) {
            resultCode = -12;
            responseModel = new LoginResponseModel(resultCode, "Password has invalid length", null);
            ServiceLogger.LOGGER.info("Password has invalid length");
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
/*
        if (email.equals("") || email == null || email.length() > 50) {
            resultCode = -10;
            responseModel = new LoginResponseModel(resultCode, "Email address has invalid length.", null);
            ServiceLogger.LOGGER.info("Email address has invalid length");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }
        */
        boolean emailUsed = RegisterUser.checkEmailInUse(email);
        if (emailUsed){
            ServiceLogger.LOGGER.info("Email exists, checking if password matches");
            //Email exists
            String query = "SELECT user_id, email, status, plevel, salt, pword" +
                           " FROM user" +
                           " WHERE email LIKE ?;";

            String pass = "";
            String salt;
            byte [] decodedSalt = null;
            try {
                PreparedStatement ps = IDMService.getCon().prepareStatement(query);
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                while (rs.next()){
                    pass = rs.getString("pword");
                    salt = rs.getString("salt");
                    decodedSalt = Hex.decodeHex(salt);

                }

                //byte [] decodedSalt = Hex.decodeHex(salt);
                byte [] hashedPassword = Crypto.hashPassword(password, decodedSalt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
                String encodedPassword = Hex.encodeHexString(hashedPassword);
                ServiceLogger.LOGGER.info("Database password: " + pass);
                ServiceLogger.LOGGER.info("Comparing with this password: " + encodedPassword);
                if (!encodedPassword.equals(pass)){
                resultCode = 11;
                responseModel = new LoginResponseModel(resultCode, "Passwords do not match", null);
                ServiceLogger.LOGGER.info("Passwords don't match");
                return Response.status(Response.Status.OK).entity(responseModel).build();

                }

                else {
                    ServiceLogger.LOGGER.info("Passwords match!");
                    resultCode = 120;
                    Session session = Session.createSession(email);
                    Timestamp time_created = session.getTimeCreated();
                    Timestamp last_used = session.getLastUsed();
                    Timestamp expr_time = session.getExprTime();
                    Token sessionID = session.getSessionID();
                    LoginUser.updateSession(sessionID, email, time_created, last_used, expr_time);
                    responseModel = new LoginResponseModel(resultCode, "User logged in successfully", sessionID.toString());
                    ServiceLogger.LOGGER.info("User logged in successfully");
                    return Response.status(Response.Status.OK).entity(responseModel).build();//.header("email", email).header("session_id", sessionID.toString()).build();





                }

            }

            catch(SQLException e){
                ServiceLogger.LOGGER.info("SQL query failed");
            }
            catch(DecoderException ee){
                ServiceLogger.LOGGER.info("Decoding failed");


            }


        }

        if (!emailUsed){
            resultCode = 14;
            responseModel = new LoginResponseModel(resultCode, "User not found", null);
            ServiceLogger.LOGGER.info("User not found");
            return Response.status(Response.Status.OK).entity(responseModel).build();


        }


        return Response.status(Response.Status.OK).entity(null).build();
    }

}
