# lwm2m-devicedata-listener

To build and run the project:

1. **Build the Project:** Run `mvn clean install` to compile and package the JAR file.
2. **Apply Custom Configuration:** If needed, modify the default settings by editing the `src/main/resources/application.properties` file.
3. **Run the JAR File:** Execute the compiled JAR to start the application.

### User Roles Setup for Listener
To ensure the listener functions correctly, make sure the following roles are assigned to your tenant’s user:

- `ROLE_NOTIFICATION_2_ADMIN`
- `ROLE_MQTT_SERVICE_ADMIN`

You can assign these roles manually via API calls:

For `ROLE_MQTT_SERVICE_ADMIN`:
```bash
curl --location '<BASE_URL>/user/<tenant>/users/<user>/roles' \
--header 'Authorization: Basic <KEY>' \
--header 'Content-Type: application/json' \
--data '{
    "role": {
        "self": "<BASE_URL>/user/roles/ROLE_MQTT_SERVICE_ADMIN",
        "id": "ROLE_MQTT_SERVICE_ADMIN"
    }
}'
```

For `ROLE_NOTIFICATION_2_ADMIN`:
```bash
curl --location '<BASE_URL>/user/<tenant>/users/<user>/roles' \
--header 'Authorization: Basic <KEY>' \
--header 'Content-Type: application/json' \
--data '{
    "role": {
        "self": "<BASE_URL>/user/roles/ROLE_NOTIFICATION_2_ADMIN",
        "id": "ROLE_NOTIFICATION_2_ADMIN"
    }
}'
```