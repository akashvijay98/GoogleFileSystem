package com.cloud.gfs;

import com.cloud.gfs.service.FileHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class GfsApplication {

	public static void main(String[] args) {
		FileHandler obj = new FileHandler();

//		String filePath = "C:/Users/ajayv/Documents/mankathatestvideo.mp4";
//		File file = new File(filePath);



		String file1 = "C:/Users/ajayv/Documents/mankathafile4644360615660218442.mp4";
		File fileobj1 = new File(file1);

		String file2 = "C:/Users/ajayv/Documents/mankathafile4097789854069479692.mp4";
		File fileobj2 = new File(file2);



		String file3 = "C:/Users/ajayv/Documents/mankathafile17529549262459857674.mp4";
		File fileobj3 = new File(file3);

		List<File> list = new ArrayList<>();
		list.add(fileobj1);
		list.add(fileobj2);
		list.add(fileobj3);

		try {
			obj.join(list);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

//		try {
//			obj.fileWriter(file, 4000000);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}

		//SpringApplication.run(GfsApplication.class, args);

	}

}
