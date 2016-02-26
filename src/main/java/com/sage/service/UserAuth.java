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

import java.io.IOException;
import java.util.Arrays;


/**
 * Created by root on 2/26/16.
 */
public class UserAuth {

    public class InvalidIdTokenException extends Exception {
        public InvalidIdTokenException(String idTokenString) {
            super("The given google id token " + idTokenString + " was invalid!");
        }
    }

    // TODO: Move client-id and client secret into config file
    private static final String CLIENT_ID = "665551274466-k9e5oun21che7qamm2ct9bn603dss65n.apps.googleusercontent.com";

    private static final String CLIENT_SECRET = "T3MOi4HvzoAo-ayP3Mv-g6TT";

    private GoogleIdTokenVerifier verifier;


    /**
     * Default constructor for a UserAuth
     * @throws Exception If a googleIdTokenVerifier could not be properly created or an HttpTransport
     * could not be established
     */
    public UserAuth() throws Exception
    {
        // setup the HttpTransport
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        // setup the JsonFactory
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
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
     * @param idTokenStr GoogleIdToken representing a sage user
     * @return User corresponding to given token
     * @throws Exception in the event the token is bad
     * or accounts.google.com is unresponsive
     */
    public User validateUser(String idTokenStr) throws Exception {
        // TODO: Add Database query logic and update logic for new potential user
        // create a null user
        System.out.println("Attempting validation...");


        User user = null;
        //idTokenStr = "{ id_token : \"hp2zvRtGee9gfn21YLJFW9_sdnwCN1VLI-B2ShOYhF0\" } ";
        // grab the idToken from google
        GoogleIdToken idToken = verifier.verify(idTokenStr);
        if (idToken != null) {
            System.out.println("The idToken is not null");
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            System.out.println(name + " - " + email);
            // Temporary - Create User from scratch
            user = new User();
            user.setUserId(1);
            user.setUserName(name);
            user.setUserEmail(email);


        } else {
            System.out.println("Invalid ID token.");
            throw new InvalidIdTokenException(idTokenStr);
        }
        // return the user
        return user;
    }


}
