
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
        Socket socket = serverSocket.accept();
        System.out.println("server accepted");

		int chunkSize = 8192;

		OutputStream outputStream = socket.getOutputStream();
        DataOutputStream  dataOutputStream = new DataOutputStream(outputStream);


		InputStream inputStream =  socket.getInputStream();
		DataInputStream readFromClient  = new DataInputStream(inputStream);


		while(true)
		{

			String command = readFromClient.readUTF();
			System.out.println("command=="+command);

			if(command == null)
			{
				dataOutputStream.writeUTF("NoCommand");
			}

			if(command.equals("create"))
			{
				dataOutputStream.writeUTF("command recived");

            	System.out.println("entered while loop");

				dataOutputStream.writeUTF("ready to create chunk");

				try {
					String fileName = readFromClient.readUTF();
					dataOutputStream.writeUTF(fileName);
					String extension = readFromClient.readUTF();
					dataOutputStream.writeUTF(extension);
					int bytes = 0;

					int chunkNo = readFromClient.readInt();
					int size = readFromClient.readInt();
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
					String path = "/home/akvj98/" + fileName + Integer.toString(chunkNo) + extension;
					System.out.println("path=" + path);
					System.out.println("buffer Array ==" + Arrays.toString(buffer));

					FileOutputStream fileOutputStream = new FileOutputStream(path);

					byte[] upbyte = new byte[bytes];
					System.arraycopy(buffer, 0, upbyte, 0, bytes);

					try {

						fileOutputStream.write(upbyte, 0, bytes);
						System.out.println("Succesfully wrote chunk" + chunkNo);
						dataOutputStream.writeUTF("success");
						dataOutputStream.flush();


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

			else if(command.equals("read"))
			{
				System.out.println("command==="+command);
				dataOutputStream.writeUTF("command recived");

				String chunkName = readFromClient.readUTF();

				String path = "/home/akvj/"+chunkName+"/";

				FileInputStream iso = new FileInputStream(path);
				byte[] buffer = new byte[4096];
				int bytes=0;
				//System.out.println("filedata====="+iso.read(buffer, 0 ,4096));

				while((bytes = iso.read(buffer, 0, 4096)) != -1)
				{

					System.out.println("buffer array =="+buffer.toString());
					dataOutputStream.write(buffer, 0, 4096);

				}

				dataOutputStream.writeUTF("successfully uploaded");
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

