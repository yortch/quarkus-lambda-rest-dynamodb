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

## Build commands (JVM build)

JVM Build command:

```
./mvnw clean install
```

Native Build command:

```
./mvnw clean install -Dnative -DskipTests -Dquarkus.native.container-build=true
```

## Deploy commands

First deploy pre-requisites (i.e. DynamoDB and IAM Role) using this command:

```
sam deploy -t iac/sam-prereqs.yaml --config-env prereqs
```

Then run deploy command for corresponding build:

* Deploy JVM Build command:

```
sam deploy -t iac/sam.yaml --config-env jvm
```

* Deploy Native Build command:

```
sam deploy -t iac/sam.yaml 
```

## Troubleshooting native build errors

* "Error: Classes that should be initialized at run time got initialized during image building"

If the following error appears:

```
Error: Classes that should be initialized at run time got initialized during image building:
 java.security.SecureRandom the class was requested to be initialized at run time (from command line with 'java.security.SecureRandom'). To see why java.security.SecureRandom got initialized use --trace-class-initialization=java.security.SecureRandom
```

Then re-run the build with the following command:

```
./mvnw clean install -Dnative -DskipTests -Dquarkus.native.container-build=true -Dquarkus.native.additional-build-args="--trace-object-instantiation=java.security.SecureRandom"
```

Solved by adding to pom.xml:

```
<quarkus.native.additional-build-args>--initialize-at-run-time=java.security.SecureRandom</quarkus.native.additional-build-args>      
```

## Related Guides

- AWS Lambda Gateway REST API ([guide](https://quarkus.io/guides/amazon-lambda-http)): Build an API Gateway REST API with Lambda integration
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing JAX-RS and more
- Amazon DynamoDB Enhanced ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-dynamodb.html)): Connect to Amazon DynamoDB datastore


