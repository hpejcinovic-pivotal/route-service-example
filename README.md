#Route Service Example 

Example focusing on securing a REST web service with Cloud Foundry route-service feature

## Route Service Overview

This is an example of how to use route service for Cloud Foundry. Example focuses on how to secure a rest web service with 
a security service. Implementation is done via shared secret token. You can extend this to use OAuth etc. Examples are done with Spring Boot. 

If you want to invoke a protected web service, you need to invoke it with the secret token passed via HTTP header, otherwise you get 404.
These examples were developed on bosh-lite, but should work on PCF 1.7+, CF 230+ 

There are three projects to demonstrate this 

- protected-rest-service : is the service we want to protect from public access
- security-service : implements the security of the protected-rest-service. It will only let you invoke it, if you provide the token
- rest-client : sample rest client that wants to consume the protected-rest-service by passing in the secret token

## Requirements
CF like environment or Bosh-lite (cf/230+) (diego/0.1452.0+)

java 1.8 +

gradle

cf cli 6.15.0+29a6079-2016-02-23 +

## Getting Started

- clone this repo
- open three terminals

- from terminal 1: 
~~~
cd protected-rest-service
gradle build
cf push --no-start
cf enable-diego protected-rest-service
cf start protected-rest-service
cf tail protected-rest-service
~~~

- from terminal 2:
~~~
cd security-service
gradle build
cf push --no-start
cf enable-diego security-service
cf start security-service
cf tail security-service
~~~

~~~
cf create-user-provided-service sec-route-service -r https://security-service.bosh-lite.com
cf bind-route-service bosh-lite.com sec-route-service --hostname protected-rest-service
~~~

1. test the security-service from Postman (www.getpostman.com) or similar by invoking
https://protected-rest-service.bosh-lite.com/greeting?name=jo
2. You should get HTTP 404
3. now try https://protected-rest-service.bosh-lite.com/greeting?name=jo
but add a http header in the request
x-cf-greetingServiceAuthToken: A59MDEBgiKP5nqiky5muIA7uAGGdyvKpZmerrczPXAwGDPmnAPKAzqwcoEliS0DsV2o3jhg2A0r4du
4. you should get back 
{"id":1,"content":"Hello, jo!"}



- If you'd like to invoke the protected-rest-service from another rest client via api you can use the third project rest-client
from terminal 3: 
~~~
cd rest-client
gradle build 
cf push --no-start
cf enable-diego rest-client
cf start rest-client
~~~

The token in this service is provided in manifest.yml To test the service you can use postman and invoke
https://rest-client.bosh-lite.com/sayHi?name=h you should get something like
Response from REST Client:{"id":3,"content":"Hello, h!"}

 





          
