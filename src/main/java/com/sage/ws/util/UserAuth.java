package com.sage.ws.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sage.ws.dao.Dao;
import com.sage.ws.dao.SageTokenDao;
import com.sage.ws.dao.UserDao;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.SageToken;
import com.sage.ws.models.User;
import com.sage.ws.models.UserCredential;
import com.sage.ws.service.SageServletContextListener;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.*;


/**
 * Created by root on 2/26/16.
 */
public class UserAuth {

    private static final Logger logger = LogManager.getLogger(UserAuth.class);

    public class InvalidIdTokenException extends Exception {
        public InvalidIdTokenException(String idTokenString, String problem) {
            super("The given google id token " + idTokenString + " was invalid with problem: " + problem);
        }
    }

    private String clientId;

    private JsonFactory jsonFactory;

    private HttpTransport transport;

    private GoogleIdTokenVerifier verifier;

    // auto-generate key TODO: Pull the key from some config file
    private static final Key key = MacProvider.generateKey();



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
     *  Verifies the given SageToken string and returns its claimed user
     * @param sageTokenStr Token string of the token to validate
     * @return The user related to the token, null if the token is invalid
     * @throws Exception If something goes wrong
     */
    public User verifySageToken(String sageTokenStr) throws Exception {
        // create a null user
        User user = null;
        try {
            logger.debug("sageTokenStr: " + sageTokenStr);
            //logger.debug("plainTextJws: " + Jwts.parser().parsePlaintextJws(sageTokenStr));
            Jwts.parser().setSigningKey(key).parseClaimsJws(sageTokenStr);
            //OK, we can trust this JWT - pull out claims
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(sageTokenStr).getBody();
            // pull userId out of claims
            int userId = Integer.parseInt(claims.getSubject()); logger.debug("userId: " + userId);
            // retrieve user from the datastore
            Dao<User> userDao = new UserDao();
            user = userDao.get(userId);
        } catch (SignatureException e) {
            // don't trust the JWT!
            logger.error("SageToken string JWT Signature Exception was thrown. Bad JWT!");
            logger.debug("Error: ", e);
        } catch (Exception e) {
            // some other error occurred
            logger.error("An error occurred while attempting to verify the SageToken string.");
            logger.debug("Error: ", e);
        }
        // return the User
        return user;
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
            crits.add(Restrictions.eq("userEmail", payload.getEmail()));  logger.debug("User Email: " + payload.getEmail());
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

    /**
     * Returns a SageToken for the given user credentials
     * @param cred UserCredential containing user googleId string and other identifying junk
     * @return SageToken object containing JWT string
     */
    public SageToken getSageToken(UserCredential cred) throws Exception {
        // verify the google token
        User user = verifyGoogleToken(cred.getGoogleIdStr());
        // return null if the verification failed
        if (user == null) return null;

        // ** BOILER PLATE **
        // We need a signing key, so we'll create one just for this example. Usually
        // the key would be read from your application configuration instead.
        //Key key = MacProvider.generateKey();
        JwtBuilder jwtBuilder = Jwts.builder();
        String tknString = jwtBuilder
                .setSubject("AuthToken")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))// nasty code +3600000 for an hour's expiration date
                //.setIssuer("Sage")// issuer Sage
                //.setId(Integer.toString(user.getUserId()))// user id
                .setSubject(Integer.toString(user.getUserId()))
                //.setIssuedAt(new Date()) //set the issued date to now
                .signWith(SignatureAlgorithm.HS512, key).compact();// sign and compact

        // set the tknString
        SageToken token = new SageToken();
        token.setSageTokenStr(tknString);

        // return the SageToken
        return token;

    }


}
