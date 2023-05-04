package com.quarkus.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(DynamodbResource.class)
public class FruitSyncServiceTest {
    
    @Inject
    FruitSyncService fruitSyncService;

    @Test
    public void testAdd() {
        Fruit fruit = new Fruit();
        fruit.setDescription("Juicy");
        fruit.setName("Mango");

        fruitSyncService.add(fruit);

        Fruit actual = fruitSyncService.get(fruit.getName());

        assertEquals(fruit.getName(), fruit.getName());
        assertEquals(fruit.getDescription(), actual.getDescription());

        //clean up
        fruitSyncService.remove(fruit.getName());
    }
  
    @Test
    public void testGetAll() {
        Fruit guava = new Fruit();
        guava.setDescription("Tropical");
        guava.setName("Guava");

        Fruit papaya = new Fruit();
        papaya.setDescription("Tasty");
        papaya.setName("Papaya");

        fruitSyncService.add(guava);
        fruitSyncService.add(papaya);

        List<Fruit> allFruits = fruitSyncService.findAll();
        assertEquals(allFruits.size(), 2);

        //clean up
        fruitSyncService.remove(guava.getName());
        fruitSyncService.remove(papaya.getName());
    }

 }
