package com.quarkus.demo;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@RegisterForReflection
public class Fruit {

    private String name;
    private String description;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("fruitName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}