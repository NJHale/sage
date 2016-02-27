package com.sage.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sage.models.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;


/**
 * Created by root on 2/26/16.
 */
public class UserAuth {

    private static final Logger logger = LogManager.getLogger("UserAuth");

    public class InvalidIdTokenException extends Exception {
        public InvalidIdTokenException(String idTokenString, String problem) {
            super("The given google id token " + idTokenString + " was invalid with problem: " + problem);
        }
    }

    //665551274466-15p0nifusupk4r9rjgrdtq773ua6m2b8.apps.googleusercontent.com
    // TODO: Move client-id and client secret into config file
    private static final String CLIENT_ID = "665551274466-k9e5oun21che7qamm2ct9bn603dss65n.apps.googleusercontent.com";
    //X21gLK_nImHfJLuEVsgqASBf
    private static final String CLIENT_SECRET = "T3MOi4HvzoAo-ayP3Mv-g6TT";

    private JsonFactory jsonFactory;

    private HttpTransport transport;

    private GoogleIdTokenVerifier verifier;



    /**
     * Default constructor for a UserAuth
     * @throws Exception If a googleIdTokenVerifier could not be properly created or an HttpTransport
     * could not be established
     */
    public UserAuth() throws Exception
    {
        // setup the HttpTransport
        transport = GoogleNetHttpTransport.newTrustedTransport();
        // setup the JsonFactory
        jsonFactory = JacksonFactory.getDefaultInstance();
        // instantiate the token verifier
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(CLIENT_ID))
                // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
                // the issuer to "https://accounts.google.com". Otherwise, set the issuer to
                // "accounts.google.com". If you need to verify tokens from multiple sources, build
                // a GoogleIdTokenVerifier for each issuer and try them both.
                .setIssuer("https://accounts.google.com")
                .build();
    }

    /**
     *
     * @param tokenStr GoogleIdToken representing a sage user
     * @return User corresponding to given token
     * @throws Exception in the event the token is bad
     * or accounts.google.com is unresponsive
     */
    public User verifyToken(String tokenStr) throws Exception {
        // TODO: Add Database query logic and update logic for new potential user
        // create a null user
        logger.debug("Attempting validation...");
        Payload payload = null;
        String problem = null;

        try {
            GoogleIdToken token = GoogleIdToken.parse(jsonFactory, tokenStr);
            payload = token.getPayload();
            // verify the token and make sure it is valid
            if (verifier.verify(token)) {
                GoogleIdToken.Payload tempPayload = token.getPayload();
                if (!tempPayload.getAudience().equals(CLIENT_ID))
                    problem = "Audience mismatch";
                else if (!CLIENT_ID.equals(tempPayload.getAuthorizedParty()))
                    problem = "Client ID mismatch";
                else
                    payload = tempPayload;
            } else {
                logger.debug("Not valid with google!");
            }
        } catch (GeneralSecurityException e) {
            problem = "Security issue: " + e.getLocalizedMessage();
        } catch (IOException e) {
            problem = "Network problem: " + e.getLocalizedMessage();
        }

        if (problem != null) logger.debug("PROBLEM: " + problem);

        User user = null;

        if (payload != null) {
            // TODO: Look for User in DB, create if doesn't exist
            user = new User();
            user.setUserEmail(payload.getEmail());
            logger.debug(payload.getEmail());
        } else {
            logger.debug("payload is null");
        }

        return user;

    }


}
