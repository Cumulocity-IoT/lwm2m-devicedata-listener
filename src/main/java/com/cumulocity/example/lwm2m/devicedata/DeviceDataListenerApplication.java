package com.cumulocity.example.lwm2m.devicedata;

import com.cumulocity.example.lwm2m.devicedata.config.ApplicationPropertiesConfig;
import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan({
		"com.cumulocity.example.lwm2m.devicedata"
})
@MicroserviceApplication
@Import({
		ApplicationPropertiesConfig.class
})
public class DeviceDataListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeviceDataListenerApplication.class, args);
	}

}
