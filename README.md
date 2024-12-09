# lwm2m-devicedata-listener

To build and run the project:

1. **Build the Project:** Run `mvn clean install` to compile and package the JAR file.
2. **Apply Custom Configuration:** If needed, modify the default settings by editing the `src/main/resources/application-default.properties` file. Set the `C8Y.baseURL` and `C8Y.baseURL.mqtt` properties to the appropriate Cumulocity platform URL and Cumulocity MQTT web service URL, respectively.  
3. **Run the Microservice:** To execute the microservice, use the microservice deployer to upload the built `lwm2m-devicedata-listener-<version>-SNAPSHOT.zip` file. The microservice uses the credentials of the tenant and user who deploys it.

### User Roles Setup for Listener
**Ensure Correct Roles Are Assigned:** To ensure the listener functions correctly, make sure the following roles are assigned to your microservice, as defined in the `cumulocity.json` file:

- `ROLE_INVENTORY_READ`
- `ROLE_NOTIFICATION_2_ADMIN`
- `ROLE_MQTT_SERVICE_ADMIN`
