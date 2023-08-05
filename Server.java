
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
			System.out.println("messagedata=="+messageData);

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
					int totalBytesRead = 0;

					String path = "/temp/" + fileName + Integer.toString(chunkNo) + extension;
					System.out.println("path=" + path);
					//System.out.println("buffer Array ==" + Arrays.toString(buffer));

					FileOutputStream fileOutputStream = new FileOutputStream(path);

					while(totalBytesRead < size && (bytes = inputStream.read(buffer,totalBytesRead,size-totalBytesRead))!=-1) {

						System.out.println("bytes ===" + bytes);

						//byte[] upbyte = new byte[bytes];
					//	System.arraycopy(buffer, 0, upbyte, 0, bytes);

						try {

							fileOutputStream.write(buffer, totalBytesRead, bytes);
							totalBytesRead += bytes;
							System.out.println("Succesfully wrote chunk" + chunkNo);

							dataOutputStream.writeUTF("success");
							dataOutputStream.flush();

							messageData=null;
						//	buffer = new byte[8192];


						}

						catch (Exception e)
						{
							e.printStackTrace();
						}

					}

					// Get the memory bean
					MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

					// Get the heap memory usage
			//		MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

					// Get the non-heap memory usage
					//	MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

					// Print the memory usage
					//	System.out.println("Heap Memory Usage: " + heapMemoryUsage);
					//System.out.println("Non-Heap Memory Usage: " + nonHeapMemoryUsage);





				}

				catch (Exception e)
				{
					e.printStackTrace();
				}

	   		}

			else if(message[0].equals("read"))
			{
				System.out.println("command==="+message[0]);

				String chunkName = message[1];

				System.out.println("chunkName="+chunkName);
				String path = "/temp/"+chunkName;
				System.out.println("file path="+path);

				FileInputStream iso = new FileInputStream(path);
				byte[] buffer = new byte[8192];
				int bytes;
				//System.out.println("filedata====="+iso.read(buffer, 0 ,8192));


				bytes = iso.read(buffer,0,8192);


					System.out.println("bytes ==" + bytes);
					byte[] upbytes = new byte[bytes];
					System.arraycopy(buffer, 0, upbytes, 0, bytes);
					System.out.println("bytes size =="+bytes);


					dataOutputStream.write(buffer, 0, bytes);
					dataOutputStream.flush();




			}
					
		}
		

    }

    public static void main(String[] args) throws IOException
	{

        Server server = new Server();
        server.run();

    }

}

