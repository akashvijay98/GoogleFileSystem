package com.cloud.gfs;

import com.cloud.gfs.service.GFSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

//import java.io.File;
import java.io.IOException;
//import java.net.Socket;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cloud.gfs", "Util"})
public class GfsApplication {

//	@Autowired
//	GFSService obj;

	@Value("${app.gfs.servers}")
	String[] servers;

	@Value("${app.gfs.ports}")
	Integer[] ports;
	public static void main(String[] args)  {


		//String filePath = "C:/Users/ajayv/Documents/fileReadJava.txt";
		//File file = new File(filePath);


		SpringApplication.run(GfsApplication.class, args);

	}



}
