package com.quarkus.demo;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

@QuarkusTest
@QuarkusTestResource(DynamodbResource.class)
public class FruitResourceTest {

    @Inject
    Logger logger;

    @Inject
    ObjectMapper objectMapper;

    @Test
    public void testGetAllEndpoint() {
        given()
            .when().get("/fruits")
            .then()
            .statusCode(200)
            .body("$.size()", is(0));
    }

    @Test
    public void testAddEndpoint() {
        Fruit mango = new Fruit();
        mango.setName("Mango");
        mango.setDescription("Juicy");
        given()
            .header("Content-Type", MediaType.APPLICATION_JSON)
            .body(getJson(mango))
            .when()
            .post("/fruits/")
            .then()
            .statusCode(200)
            .body("$.size()", is(1),
            "name", containsInAnyOrder(mango.getName()),
                "description", containsInAnyOrder(mango.getDescription()));;
    }

    private String getJson(Fruit fruit) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(fruit);
        } catch (Exception e) {
            logger.warn("Error serializing to json: " + e.getMessage(), e);
        }
        return json;
    }
}