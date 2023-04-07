# quarkus-lambda-rest-dynamodb

This project uses Quarkus to demo using Quarkus Lambda REST extension which exposes a REST API with connecting to DynamoDB 

## Prerequisites

- SAM CLI
- AWS CLI
- maven or gradle (this guide uses mvn and can also use provided maven wrapper - `mvnw`)
- jdk 11+
- Docker (required for native build)

## Run locally

Builds, runs tests and starts application locally on `http://localhost:8080`

```
./mvnw quarkus:dev
```

## Build/deploy commands (JVM build)

Run build command:

```
mvn clean install
```

Run deploy command:

```
sam deploy -t iac/sam.yaml --config-env jvm
```

## Build/deploy commands (Native build)

Run build command:

```
mvn clean install -Dnative -DskipTests -Dquarkus.native.container-build=true
```

Run deploy command:

```
sam deploy -t iac/sam.yaml 
```


## Related Guides

- AWS Lambda Gateway REST API ([guide](https://quarkus.io/guides/amazon-lambda-http)): Build an API Gateway REST API with Lambda integration
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing JAX-RS and more
- Amazon DynamoDB Enhanced ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-dynamodb.html)): Connect to Amazon DynamoDB datastore


