package com.sage.ws.resources;

import com.sage.ws.models.Goat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 2/18/16.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/goats")
public class GoatsResource {

    private static final Logger logger = LogManager.getLogger(GoatsResource.class);

    @GET
    public List<Goat> getGoats(
            @QueryParam("age") int age,
            @QueryParam("aggression") int aggression,
            @QueryParam("weight") double weight ) {
        logger.debug("Hitting GET goats/...");
        List<Goat> goats = new LinkedList<Goat>();
        for (int i = 1; i < 4; i++) {// no goats under the age of 1 permitted
            Goat goat = new Goat();
            goat.setAge(i * 10);
            goat.setWeight(i * 100);
            goat.setAggression((int) (goat.getAge() * goat.getWeight()));
            goats.add(goat);
        }

        if (age > 0) {
            for (int i = 0; i < goats.size(); i++) {
                if (goats.get(i).getAge() != age) {
                    // remove the goat
                    goats.remove(i);
                    // correct index
                    i--;
                }
            }
        }
        logger.debug("Returning from GET goats/...");
        return goats;
    }

}
