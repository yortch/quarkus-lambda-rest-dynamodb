# quarkus-lambda-rest-dynamodb

This project uses Quarkus to demo using Quarkus Lambda REST extension which exposes a REST API with connecting to DynamoDB 

## Prerequisites

- SAM CLI
- AWS CLI
- maven or gradle (this guide uses mvn and can also use provided maven wrapper - `mvnw`)
- jdk 11+
- Docker (required for native build and running locally)

## Run locally

First we need to create a local dynamodb instance and configure it as follows:

```
docker run --rm --name local-dynamo -p 8000:4566 -e SERVICES=dynamodb -e START_WEB=0 -d localstack/localstack
```

Alternatively, if docker is not available, DynamodbLocal can be downloaded from [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html#DynamoDBLocal.DownloadingAndRunning.title). Extract downloaded file and run the following command from extract directory:

```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -inMemory
```

Next configure profile for localstack:

```
aws configure --profile localstack
AWS Access Key ID [None]: test-key
AWS Secret Access Key [None]: test-secret
Default region name [None]: us-east-1
```

Next create the dynamodb table:

```
aws dynamodb create-table --table-name QuarkusFruits \
    --attribute-definitions AttributeName=fruitName,AttributeType=S \
    --key-schema AttributeName=fruitName,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --profile localstack --endpoint-url=http://localhost:8000
```

Builds, runs tests and starts application locally on `http://localhost:8080`

```
./mvnw quarkus:dev
```

## Build commands (JVM build)

JVM/SnapStart Build command:

```
./mvnw clean install
```

Native Build command:

```
./mvnw clean install -Dnative -DskipTests -Dquarkus.native.container-build=true
```

## Deploy commands

First log into your AWS account using `aws configure`

Next deploy pre-requisites (i.e. DynamoDB and IAM Role) using this command:

```
sam deploy -t iac/sam-prereqs.yaml --config-env prereqs
```

Then run deploy command for corresponding build:

* Deploy JVM Build command:

```
sam deploy -t iac/sam.yaml --config-env jvm
```

* Deploy SnapStart command:

```
sam deploy -t iac/sam.yaml --config-env snapstart
```

* Deploy Native Build command:

```
sam deploy -t iac/sam.yaml 
```

## Test commands

You can test by exporting the service url into a variable:
```
URL=<replace url>
```

Next, submit a request to insert a fruit:

```
curl -w '\nTime: %{time_total}s\n' --location --request POST ${URL} \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "lemon",
  "description": "citrus fruit"
}'
```

Finally, execute get all fruits request:

```
curl -w '\nTime: %{time_total}s\n' --location --request GET ${URL}
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


