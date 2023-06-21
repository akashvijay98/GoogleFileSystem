package com.cloud.gfs;

import com.cloud.gfs.service.GFSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

@SpringBootApplication
public class GfsApplication {

//	@Autowired
//	GFSService obj;

	@Value("${app.gfs.servers}")
	String[] servers;

	@Value("${app.gfs.ports}")
	Integer[] ports;
	public static void main(String[] args) throws IOException {


		String filePath = "C:/Users/ajayv/Documents/fileReadJava.txt";
		File file = new File(filePath);



//		String file1 = "C:/Users/ajayv/Documents/mankathafile4644360615660218442.mp4";
//		File fileobj1 = new File(file1);
//
//		String file2 = "C:/Users/ajayv/Documents/mankathafile4097789854069479692.mp4";
//		File fileobj2 = new File(file2);
//
//
//
//		String file3 = "C:/Users/ajayv/Documents/mankathafile17529549262459857674.mp4";
//		File fileobj3 = new File(file3);
//
//		List<File> list = new ArrayList<>();
//		list.add(fileobj1);
//		list.add(fileobj2);
//		list.add(fileobj3);
//
//		try {
//			obj.joinFileChunks(list);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		try {
//			obj.uploadFile(file);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}

	//	obj.testChunk();


		SpringApplication.run(GfsApplication.class, args);

	}

//    @Bean(name = "socket1")
//	public Socket returnSocket1() throws IOException {
//
//		Socket sc1 = new Socket(servers[1], ports[1]);
//		return  sc1;
//
//	}
//
//	@Bean(name = {"socket2"})
//	public Socket returnSocket2() throws IOException {
//
//		Socket sc2 = new Socket(servers[2], ports[2]);
//		return  sc2;
//
//	}
//
//	@Bean(name = {"socket0"})
//	public Socket returnSocket0() throws IOException {
//
//		Socket sc0 = new Socket(servers[0], ports[0]);
//		return  sc0;
//
//	}


}
