package com.sage.ws.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sage.ws.dao.UserDao;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.User;
import com.sage.ws.service.SageServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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

    private String clientId;

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
        // set the clientId
        clientId = SageServletContextListener.config.googleClientId;
        // setup the HttpTransport
        transport = GoogleNetHttpTransport.newTrustedTransport();
        // setup the JsonFactory
        jsonFactory = JacksonFactory.getDefaultInstance();
        // instantiate the token verifier
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(clientId))
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
    public User verifyGoogleToken(String tokenStr) throws Exception {
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
                if (!tempPayload.getAudience().equals(clientId))
                    problem = "Audience mismatch";
                else if (!clientId.equals(tempPayload.getAuthorizedParty()))
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

        User user = null;

        if (payload != null) {
            // try to get the user by their email
            UserDao uDao = new UserDao();
            List<Criterion> crits = new ArrayList<Criterion>();
            crits.add(Restrictions.eq("userEmail", payload.getEmail()));
            List<User> users = uDao.get(crits, null, 1);
            // we should only get one result back if the user exists
            if (users.size() > 0) {
                user = users.get(0);
            }
            else {
                // we must create and save the user
                user = new User();
                user.setUserEmail(payload.getEmail());
                // save the user
                int userId = uDao.add(user);
                // cheat and set the user's id since we got it back from the save
                user.setUserId(userId);

            }
            logger.debug(payload.getEmail() + " - userId: " + user.getUserId());
        } else {
            logger.debug("payload is null");
        }

        return user;

    }


}
