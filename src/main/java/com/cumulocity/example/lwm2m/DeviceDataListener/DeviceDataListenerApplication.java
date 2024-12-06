package com.cumulocity.example.lwm2m.DeviceDataListener;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;

@MicroserviceApplication
public class DeviceDataListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeviceDataListenerApplication.class, args);
	}

}
