

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {

    void run() throws IOException{
        String serverIp = "192.168.1.19";
        int port = 4591;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("server started");
        Socket socket = serverSocket.accept();
        System.out.println("server accepted");

	int chunkSize = 4096;

        OutputStream outputStream = socket.getOutputStream();

        DataOutputStream  writeToClient =
                new DataOutputStream(outputStream);

	//BufferedReader readFromClient =
          //      new BufferedReader(new InputStreamReader(socket.getInputStream()));

	InputStream inputStream =  socket.getInputStream();
            DataInputStream readFromClient  = new DataInputStream(inputStream);


		while(true){
		String command = readFromClient.readUTF();
		System.out.println("command=="+command);
       if(command.equals("create")){

    	              // read a message from the client
            System.out.println("entered while loop");

		writeToClient.writeUTF("ready to create chunk");
		//System.out.println("action=="+readFromClient.readUTF());
	try {
	    String fileName = readFromClient.readUTF();
		writeToClient.writeUTF(fileName);
	    String extension = readFromClient.readUTF(); 
		writeToClient.writeUTF(extension);




	   int bytes = 0;

	   int chunkNo = readFromClient.readInt();

	int size = readFromClient.readInt();
            byte[] buffer  = new byte[size];

System.out.println("size====="+size);


            bytes = inputStream.read(buffer);
                String path = "/home/akvj/"+fileName+Integer.toString(chunkNo)+extension;
		System.out.println("path="+path);
		System.out.println("buffer Array =="+Arrays.toString(buffer));

               FileOutputStream fileOutputStream = new FileOutputStream(path);

		byte[] upbyte = new byte[bytes];
		System.arraycopy(buffer,0,upbyte,0,bytes);

	//	System.out.println("array=="+( readFromClient.read(buffer)));
		try{
			fileOutputStream.write(upbyte, 0 ,bytes);
			System.out.println("Succesfully wrote chunk"+chunkNo);
				writeToClient.flush();
		}
		catch(Exception e){    e.printStackTrace();  }

	//	bytes = readFromClient.read(buffer);
	//	count++;

                 writeToClient.writeUTF("success");
	//	writeToClient.close();
	//	readFromClient.close();
		}
	

		
		catch (Exception e){ e.printStackTrace();}

	     }

            //System.out.println("Recieve message"+message);
            


             // client closed its side of the connection
           // if (message.equals("quit")) break; // client sent a quit message

            // prepare a reply, in this case just echoing the message
           // String reply = "echo: " + message;

            // write the reply
            //writeToClient.println(reply);
            //writeToClient.flush(); // important! otherwise the reply may just sit in a buffer, unsent
             
          

	else if(command.equals("read"))
	{
		System.out.println("command==="+command);
		String chunkName = readFromClient.readUTF();
 
		String path = "/home/akvj/"+chunkName+"/";

		
			FileInputStream iso = new FileInputStream(path);

			byte[] buffer = new byte[4096];
			int bytes=0;
			//System.out.println("filedata====="+iso.read(buffer, 0 ,4096));

			while((bytes = iso.read(buffer, 0, 4096)) != -1){
				System.out.println("buffer=="+buffer.toString());
				writeToClient.write(buffer, 0, 4096);

					}


			writeToClient.writeUTF("success");
		}
	}

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}

