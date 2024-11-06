package com.cumulocity.example.lwm2m.DeviceDataListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.cumulocity.example.lwm2m.DeviceDataListener"})
public class DeviceDataListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeviceDataListenerApplication.class, args);
	}

}
