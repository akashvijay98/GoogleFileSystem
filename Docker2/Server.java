
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Server {
	private static final Logger log = Logger.getLogger(Server.class.getName());

	void run() throws IOException
	{
		int port = 4592;
		ServerSocket serverSocket = new ServerSocket(port);
		log.info("server started");


		int chunkSize = 8192;



		while(true)
		{
			Socket socket = serverSocket.accept();
			log.info("server accepted");

			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream  dataOutputStream = new DataOutputStream(outputStream);

			InputStream inputStream =  socket.getInputStream();
			DataInputStream readFromClient  = new DataInputStream(inputStream);

			String messageData = readFromClient.readUTF();
			String[] message = messageData.split(",");
			log.log(Level.INFO, "messagedata={0}", messageData);

			if(message[0] == null || message[0]=="")
			{
				log.info("NoCommand");
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

					String path = "/temp2/" + fileName + Integer.toString(chunkNo) + extension;
					System.out.println("path=" + path);

					FileOutputStream fileOutputStream = new FileOutputStream(path);

					while(totalBytesRead < size && (bytes = inputStream.read(buffer,totalBytesRead,size-totalBytesRead))!=-1) {

						log.log(Level.INFO, "bytes ={0}", bytes);

						try {

							fileOutputStream.write(buffer, totalBytesRead, bytes);
							totalBytesRead += bytes;
							log.log(Level.INFO, "Succesfully wrote chunk" + chunkNo);

							dataOutputStream.writeUTF("success");
							dataOutputStream.flush();
							messageData=null;

						}

						catch (Exception e)
						{
							e.printStackTrace();
						}

					}

					// Get the memory bean
					MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

					// Get the heap memory usage
					MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

					// Get the non-heap memory usage
					MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

					log.log(Level.INFO, "Heap Memory Usage:{0} " ,heapMemoryUsage);
					log.log(Level.INFO, "Non-Heap Memory Usage:{0} " ,nonHeapMemoryUsage);

				}

				catch (Exception e)
				{
					e.printStackTrace();
				}

			}

			else if(message[0].equals("read"))
			{
				String chunkName = message[1];
				log.log(Level.INFO, "chunkName=", chunkName);

				String path = "/temp1/"+chunkName;
				log.log(Level.INFO, "file path={0}",path);

				FileInputStream iso = new FileInputStream(path);
				byte[] buffer = new byte[8192];
				int bytes;

				bytes = iso.read(buffer,0,8192);


				log.log(Level.INFO, "bytes ={0}", bytes);
				byte[] upbytes = new byte[bytes];
				System.arraycopy(buffer, 0, upbytes, 0, bytes);

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

