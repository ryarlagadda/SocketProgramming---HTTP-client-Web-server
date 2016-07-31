//AUTHOR: RAVI TEJA YARLAGADDA (800909854)
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MyServer implements Runnable {
	private Socket clientsocket;
	private ServerSocket serverSocket;
	public boolean choice=true;
	//creating a constructor for passing the connection/socket details
	public MyServer(Socket clientsocket,ServerSocket serverSocket)
	{
			this.clientsocket=clientsocket;
			this.serverSocket=serverSocket;
	}
	public void run() {
		// TODO Auto-generated method stub
		
		try {
				Socket sock=this.clientsocket;
				InputStreamReader isr= new InputStreamReader(sock.getInputStream());
				BufferedReader br=new BufferedReader(isr);
				//reading the request from the client
				String msg=br.readLine();
				//for a PUT request when a file is not found client sends a N/A message 
				if(msg.equals("N/A"))
				{
					System.out.println("in N/A");
					this.sendPutReponse("FILE NOT FOUND");
					System.out.println("client :"+ clientsocket.getRemoteSocketAddress()+"successfully closed..!!!!");
				}
				//getting the file name from the request
				StringTokenizer st=new StringTokenizer(msg," ");
				String[] s =new String[2];
				int i=0;
				System.out.println("THE REQUEST RECEIVED IS: "+msg);
				while(st.hasMoreTokens()&&i<=1)
				{
					s[i]=st.nextToken();					
					i++;
				}
				
				if(s[0].equals("GET"))
				{
					File fl=new File(s[1]);
					if(fl.exists())
					{
						//reading the file and sending it to client
						int m;
						System.out.println("Received a GET request from client..!!!");
						System.out.println("File exists: "+s[1]);
						FileInputStream inputStream = new FileInputStream(s[1]);
						StringBuilder sbr=new StringBuilder();
						while((m=inputStream.read())!=-1)
						{
							sbr.append((char)m);
						}
						String readfileContent=sbr.toString();
		        		//System.out.println("The File contents are: "+"\n"+readfileContent);
		        		System.out.println("The Requested file is sent to client");
		        		//calling the sendGetResponse method to send the response to client 
		        		this.sendGetResponse(readfileContent);
					}
					else
					{
						this.sendGetResponse("Not Found");
					}
				}
				else if(s[0].equals("PUT"))
				{
					String fname=s[1];
					//getting the extension of the file to create the file with same extension at server
					String extension[]=fname.split("\\.");
					StringBuilder sb=new StringBuilder();
					String putstring="";
					System.out.println("The file sent by Client is: "+fname);
					String outputFile="output."+extension[1];
					File result=new File(outputFile);
					FileWriter fw=new FileWriter(result);
					while(true)
					{
						msg=br.readLine();
					//System.out.println(msg);
					sb.append(msg);
					if(msg.contains("File Data is : "))
					{
						while(true)
						{
						msg=br.readLine();
						System.out.println(msg);
						//when it reaches to end of file contents ending the loop and writing contents to file.
						if(msg.equals("End of file contents"))
						{
							fw.close();
							break;
						}
						fw.write(msg);
					}
					}
					if(msg.equals("End of file contents"))
					{
						break;
					}
					}
					//calling sendputresponse method to send response to client
					this.sendPutReponse(outputFile);
				}
				else if (msg.equals("N/A")) {
					this.sendPutReponse("FILE NOT FOUND");
				}
				else
				{
					if(s[1].equals("closed"))
					{
						System.out.println("client: "+clientsocket.getRemoteSocketAddress()+" succesfully closed");
					}
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket Could not be created :"+e.getMessage());
		}
	}
	private void sendPutReponse(String outputFile) {
		// TODO Auto-generated method stub
		try {
			//sending the put request response to client
			Socket sock=this.clientsocket;
			PrintStream ps=new PrintStream(sock.getOutputStream());
			if(outputFile.equals("FILE NOT FOUND"))
			{
				System.out.println("FILE NOT FOUND.....");
				System.out.println("ERROR:404");
				System.out.println("THE REQUESTED FILE IS NOT FOUND AT THE CLIENT SIDE");	
			}
			else{
			ps.println("FILE CREATED SUCCESSFULLY AT THE SERVER LOCAL: 200 OK");
			System.out.println("FILE CREATED : 200 OK");
			System.out.println("The file received from the Client is Successfully stored in your local as: "+outputFile);
			}
			ps.println("close");
			ps.flush();
			ps.close();
			System.out.println("Successfully Closed the client connection: "+sock.getRemoteSocketAddress());
			System.out.println("DO YOU WISH TO CLOSE the SERVER..!!! => Press 1 / IF YOU WISH TO CONTINUE JUST IGNORE THIS MESSAGE.......!!!!!!");
			Scanner sc=new Scanner(System.in);
			if(sc.nextInt()==1)
			{
				//closing the server connection up on a request
				this.serverSocket.close();
				System.out.println("Request to stop the server");
				System.out.println("Server Successfully Closed....!!!!");
				this.choice=false;
				System.exit(0);
			}
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void sendGetResponse(String readfileContent) {
		// TODO Auto-generated method stub
		try {
			//sending get request response to client
			Socket sock=this.clientsocket;
			PrintStream ps=new PrintStream(sock.getOutputStream());
			if(readfileContent.equals("Not Found"))
			{
				System.out.println("ERROR:404 NOT FOUND");
				System.out.println("THE REQUESTED FILE IS NOT FOUND....!!!");
				ps.println("ERROR: 404 NOT FOUND");
				ps.println("Sorry the requested file is not found....!!!!");
			}
			else
			{
				//sending the filecontents to client
				ps.println("The Requested File Exits: 200 OK");
				ps.println("The file contents are:");
				ps.println(readfileContent);
				ps.println("Data Successfully retrieved from the server..!!!!!");		
			} 
			
			ps.println("close");
			System.out.println("Successfully Closed the client connection: "+sock.getRemoteSocketAddress());
			System.out.println("DO YOU WISH TO CLOSE the SERVER..!!! => PRESS 1 / IF YOU WISH TO CONTINUE JUST IGNORE THIS MESSAGE.......!!!!!!");
			Scanner sc=new Scanner(System.in);
			if(sc.nextInt()==1)
			{
				//closing server connection up on request
				this.serverSocket.close();
				System.out.println("Request to stop the server");
				System.out.println("Server Successfully Closed....!!!!");
				this.choice=false;
				System.exit(0);
			}
			
			ps.close();
			sock.close();
		}
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String args[])
	{
		if(args.length==1)
		{
		int port =Integer.parseInt(args[0]);
			try {
				//creating a server socket
				ServerSocket serverSocket= new ServerSocket(port,1,InetAddress.getByName("localhost"));
				System.out.println("Servers address is:"+serverSocket.getLocalSocketAddress());
			//server runs until an explicit stop request is received
				while(true)
				{
				//accepting the client connections
				Socket clientSocket=serverSocket.accept();
				System.out.println("Connected to client: "+clientSocket.getRemoteSocketAddress());
				//creating a thread for each incoming client connection
				Runnable r=new Thread(new MyServer(clientSocket,serverSocket));
				new Thread(r).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Server Socket closed ");
			}
		}
		else
		{
			System.out.println("Please enter the Server PORT NUMBER and Try again.....!!!!");
		}
	}
}
	
//}
