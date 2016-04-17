package com.sage.ws.resources;

import com.sage.ws.models.SageToken;
import com.sage.ws.models.User;
import com.sage.ws.models.UserCredential;
import com.sage.ws.util.UserAuth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.util.Date;

/**
 * Created by Nick Hale on 4/14/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/sageTokens")
public class SageTokensResource {

    private static final Logger logger = LogManager.getLogger(SageTokensResource.class);

    @POST
    public SageToken getSageToken(UserCredential cred) {
        SageToken token = null;
        try {
            // construct a UserAuth
            UserAuth auth = new UserAuth();
            // get a SageToken with the given credentials
            token = auth.getSageToken(cred);

            if (token == null) {
                // the googleIdToken failed verification, throw a 403 - authentication failure
                throw new WebApplicationException(403);
            }


        } catch (WebApplicationException e) {
            // rethrow exception
            logger.error("An error occurred while validating the given UserCredentials. Rethrowing exception.");
            logger.debug("Error: ", e);
            throw e;
        } catch (Exception e) {
            // some other issue occurred, throw a 500 - server error
            logger.error("Some error occurred while attempting to generate a SageToken. Throwing exception.");
            throw new WebApplicationException(500);
        }

        return token;
    }
}
