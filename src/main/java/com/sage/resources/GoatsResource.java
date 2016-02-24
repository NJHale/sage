package com.sage.resources;

import com.sage.models.Goat;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 2/18/16.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/goats")
public class GoatsResource {

    @GET
    public List<Goat> getGoats(
            @QueryParam("age") int age,
            @QueryParam("aggression") int aggression,
            @QueryParam("weight") double weight ) {
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

        return goats;
    }

}
