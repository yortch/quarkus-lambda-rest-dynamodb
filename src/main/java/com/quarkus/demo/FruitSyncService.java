package com.quarkus.demo;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@ApplicationScoped
public class FruitSyncService extends AbstractService {

    private DynamoDbTable<Fruit> fruitTable;

    private static final StaticTableSchema<Fruit> FRUIT_TABLE_SCHEMA =
        StaticTableSchema.builder(Fruit.class)
            .newItemSupplier(Fruit::new)
            .addAttribute(String.class, a -> a.name("fruitName")
                                                .getter(Fruit::getName)
                                                .setter(Fruit::setName)
                                                .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("description")
                                                .getter(Fruit::getDescription)
                                                .setter(Fruit::setDescription))
            .build();

    @Inject
    FruitSyncService(DynamoDbEnhancedClient dynamoEnhancedClient) {
        //NOTE: to improve initialization time we are using StaticSchema instead of inferring from bean
        //TableSchema.fromClass(Fruit.class)
        fruitTable = dynamoEnhancedClient.table(getTableName(), FRUIT_TABLE_SCHEMA);
    }

    public List<Fruit> findAll() {
        return fruitTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Fruit> add(Fruit fruit) {
        fruitTable.putItem(fruit);
        return findAll();
    }

    public Fruit get(String name) {
        Key partitionKey = Key.builder().partitionValue(name).build();
        return fruitTable.getItem(partitionKey);
    }

    public Fruit remove(String name) {
        Key partitionKey = Key.builder().partitionValue(name).build();
        return fruitTable.deleteItem(partitionKey);
    }
}