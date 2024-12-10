# lwm2m-devicedata-listener

### Overview
This sample project demonstrates the process of creating a microservice that utilizes the MQTT Service API to subscribe 
to and listen for LWM2M device data messages. Included in this sample is a JSON Schema file, 
`src/main/resources/schemas/LWM2MDataMessage-schema.json`, which can serve as a contract for validating the data received 
from the subscribed topic. In this example, the JSON Schema is primarily used to generate Java POJOs and to format the 
received JSON data for clear presentation in the application log. The primary goal of this sample is to validate that LWM2M 
device data is successfully received. However, users are encouraged to implement their own methods for deserializing and 
processing the received data as needed.

### Build
To build and run the project:
1. **Build the Project:** Run `mvn clean install` to compile and package the JAR file.
2. **Apply Custom Configuration:** No custom configuration needed when the microservice application is uploaded from the Administration application in Cumulocity platform.
If this is being run locally for development, modify the default settings by editing the `src/main/resources/application-default.properties` file. Set the `C8Y.baseURL` to the appropriate Cumulocity platform URL.
3. **Run the Microservice:** Upload the built `lwm2m-devicedata-<version>-SNAPSHOT.zip` file from the Administration application in Cumulocity platform as described in https://cumulocity.com/docs/standard-tenant/ecosystem/#managing-microservices.
The logs can be viewed from the application's Logs tab.

### User Roles Setup for Listener
**Ensure Correct Roles Are Assigned:** To ensure the listener functions correctly, make sure the following roles are assigned to your microservice, as defined in the `cumulocity.json` file:
- `ROLE_INVENTORY_READ`
- `ROLE_NOTIFICATION_2_ADMIN`
- `ROLE_MQTT_SERVICE_ADMIN`
