
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {

    void run() throws IOException
	{
        String serverIp = "192.168.1.19";
        int port = 4591;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("server started");
       

	int chunkSize = 8192;

		

		while(true)
		{
 			 Socket socket = serverSocket.accept();
        		System.out.println("server accepted");


			OutputStream outputStream = socket.getOutputStream();
        		DataOutputStream  dataOutputStream = new DataOutputStream(outputStream);


			InputStream inputStream =  socket.getInputStream();
			DataInputStream readFromClient  = new DataInputStream(inputStream);


			String messageData = readFromClient.readUTF();

		
			
			String[] message = messageData.split(",");
			System.out.println("command=="+message[0]);

			if(message[0] == null || message[0]=="")
			{
				System.out.println("NoCommand");
			}

			if(message[0].equals("create"))
			{

				try {
					String fileName = message[1];

					String extension = message[2];
					int bytes = 0;

					int chunkNo = Integer.parseInt(message[3]);
					int size = Integer.parseInt(message[4]);
					byte[] buffer = new byte[size];


					System.out.println("size=====" + size);


					bytes = inputStream.read(buffer);

					// Get the memory bean
					MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

					// Get the heap memory usage
					MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

					// Get the non-heap memory usage
					MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

					// Print the memory usage
					System.out.println("Heap Memory Usage: " + heapMemoryUsage);
					System.out.println("Non-Heap Memory Usage: " + nonHeapMemoryUsage);
					String path = "/C:/Users/akash/Documents/Test/" + fileName + Integer.toString(chunkNo) + extension;
					System.out.println("path=" + path);
					//System.out.println("buffer Array ==" + Arrays.toString(buffer));

					FileOutputStream fileOutputStream = new FileOutputStream(path);

					byte[] upbyte = new byte[bytes];
					System.arraycopy(buffer, 0, upbyte, 0, bytes);

					try {

						fileOutputStream.write(upbyte, 0, bytes);
						System.out.println("Succesfully wrote chunk" + chunkNo);

						dataOutputStream.writeUTF("success");
						dataOutputStream.flush();
						messageData=null;

						

					}

					catch (Exception e)
					{
						e.printStackTrace();
					}


				}

				catch (Exception e)
				{
					e.printStackTrace();
				}

	   		}

			else if(message[0].equals("read"))
			{
				System.out.println("command==="+message[0]);
				dataOutputStream.writeUTF("command recived");

				String chunkName = readFromClient.readUTF();

				String path = "/C:/Users/akash/Documents/Test/"+chunkName;
					
				System.out.println("file path="+path);

				FileInputStream iso = new FileInputStream(path);
				byte[] buffer = new byte[8192];
				int bytes=0;
				//System.out.println("filedata====="+iso.read(buffer, 0 ,8192));

				bytes = iso.read(buffer,0,8192);
				

					System.out.println("bytes =="+bytes);
					System.out.println("buffer length =="+buffer.length);


					dataOutputStream.write(buffer, 0, bytes);
					dataOutputStream.flush();


				

				//
.writeUTF("successfully uploaded");
				

			}


			

				   
					
		}
		

    }

    public static void main(String[] args) throws IOException
	{

        Server server = new Server();
        server.run();

    }

}

