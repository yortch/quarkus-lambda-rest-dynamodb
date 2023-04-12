package com.quarkus.demo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class DynamodbResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOG = Logger.getLogger(DynamodbResource.class);

    private final static String TABLE_NAME = "QuarkusFruits";

    private final static Region REGION = Region.US_EAST_1;

    private final static String ACCESS_KEY = "accessKey";

    private final static String SECRET_KEY = "secretKey";

    private final static String PORT = "8001";

    private DynamoDBProxyServer server;

    private DynamoDbClient client;

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = new HashMap<>();
        try {
            System.setProperty("sqlite4java.library.path", "target/native-libs");
            server = ServerRunner.createServerFromCommandLineArgs(new String[] { "-inMemory", "-port", PORT });
            server.start();

            String url = "http://localhost:" + PORT;
            URI endpointOverride = URI.create(url);

            LOG.debug("Initializing Local DynamoDb at url: " + url);

            client = DynamoDbClient.builder()
                    .endpointOverride(endpointOverride)
                    .credentialsProvider(
                            StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .region(REGION).build();

            String key = "fruitName";
            client.createTable(CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                    .attributeName(key)
                    .attributeType(ScalarAttributeType.S)
                    .build())
                .keySchema(KeySchemaElement.builder()
                    .attributeName(key)
                    .keyType(KeyType.HASH)
                    .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                    .readCapacityUnits(1L)
                    .writeCapacityUnits(1L)
                    .build())
                .tableName(TABLE_NAME)
                .build()
            );

            properties.put("quarkus.dynamodb.endpoint-override", endpointOverride.toString());
            properties.put("quarkus.dynamodb.aws.region", REGION.toString());
            properties.put("quarkus.dynamodb.aws.credentials.type", "static");
            properties.put("quarkus.dynamodb.aws.credentials.static-provider.access-key-id", ACCESS_KEY);
            properties.put("quarkus.dynamodb.aws.credentials.static-provider.secret-access-key", SECRET_KEY);

        } catch (Exception e) {
            throw new RuntimeException("Could not start Dynamodb local", e);
        }
        return properties;
    }

    @Override
    public void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                LOG.warn("Failed to stop dynamodb, ignoring exception: " + e.getMessage(), e);
            }
        }
    }
}