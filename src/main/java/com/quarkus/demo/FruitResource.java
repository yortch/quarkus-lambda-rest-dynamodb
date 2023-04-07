package com.quarkus.demo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    @Inject
    FruitSyncService service;

    @GET
    @Path("{fruitName}")
    public Fruit getFruit(String fruitName) {
        return service.get(fruitName);
    }

    @GET
    public List<Fruit> getAllFruits() {
        return service.findAll();
    }

    @POST
    public List<Fruit> add(Fruit fruit) {
        return service.add(fruit);
    }

}