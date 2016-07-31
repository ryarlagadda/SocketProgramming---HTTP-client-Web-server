//AUTHOR RAVI TEJA YARLAGADDA (800909854)
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyClient
{

	public void clientRequest(Socket clientSocket,String command,String filename,String hostname,int port)
	{
		if(command.equals("GET"))
		{
			try {
				//sending the request to the server
				PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println("GET "+filename+" HTTP/1.1\r\n");
				pw.print("Accept: text/plain, text/html, text/*\r\n");
				pw.println("Host: "+hostname+":" + port);
				pw.flush();
				System.out.println("GET Request Sent to Server ");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Unable to get the ouputstream...!!!! "+e.getMessage());
			}
		}
		else
		{
			//String s[]=filename.split("/");
			//filename=s[1];
			File f = new File(filename);
        	System.out.println("THE REQUESTED FILE IS:"+filename);
        	try
        	{
        	PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        	if(f.exists()){
        		//sending the requested file to server
        		System.out.println("FILE EXISTS");
        		FileInputStream fis;
					fis = new FileInputStream(f);
					StringBuilder builder = new StringBuilder();
				      int line;
				      while(( line = fis.read()) != -1 ) {
				         builder.append( (char)line );
				      }
				      String filedata=builder.toString();
					System.out.println("File Data is : "+"\n"+filedata);
					out.println("PUT "+filename+" HTTP/1.1\r\n");
					out.print("Accept: text/plain, text/html, text/*\r\n");
					out.println("Host:" +"localhost:"+ clientSocket.getLocalPort());
					out.println("File Data is : ");
					out.println(filedata);
					out.println("End of file contents");
					out.flush();
					fis.close();
        	}
			else{
			//	System.out.println("in client N/A");
				out.println("N/A");
				out.flush();
				//out.close();
				System.out.println("FILE DOES NOT EXIST "+"\n"+"ERROR: 404 NOT FOUND");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("File could not be found :"+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to get the ouputstream...!!!! "+e.getMessage());
			//e.printStackTrace();
		}
		
		
		}
	}

public int serverResponse(Socket clientSocket){
		
		try {
			//retrieving the server response
			InputStreamReader isr= new InputStreamReader(clientSocket.getInputStream());
			BufferedReader br=new BufferedReader(isr);
			String msg;
	        while ((msg = br.readLine()) != null) {
	            if(msg.equals("close"))
	            {
	            	//after the complete message is retrieved closing the client
	            	PrintWriter pw=new PrintWriter(clientSocket.getOutputStream());
	            	pw.println("closed");
	            	return 1;
	            }
	            System.out.println(msg);
	            if (msg.isEmpty()) {
	                break;
	            }
	        }
	        isr.close();
	        br.close();
	       // return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error in getting the inputstream from server "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}
//creating a constructor to pass the socket details for each request
public MyClient(Socket newsocket)
{
	Socket createdsocket=newsocket;
}

	public static void main(String args[])
	{
		String hostname,command,filename;
		int port;
		//Assigning the inputs to variables 
		if(args.length==4)
		{
			hostname=args[0];
			port=Integer.parseInt(args[1]);
			command=args[2];
			filename=args[3];
    	try {
    		//estrablishing a connection with the server
			Socket clientSocket=new Socket(hostname,port);
			//creating a new class object 
			MyClient request=new MyClient(clientSocket);
		    if(command.equals("GET")||command.equals("PUT"))
		    {
		    	System.out.println("Connected to the Server: "+clientSocket.getRemoteSocketAddress());
		    	//sending the client request to the server
		    	request.clientRequest(clientSocket,command, filename,hostname,port);
		    	//retrieving the response from the server
		    	int close=request.serverResponse(clientSocket);
		    	if(close==1)
		    	{
		    		//closing the client connection 
					clientSocket.close();
		    	System.out.println("CLIENT CONNECTION IS SUCCESFULLY CLOSED....!!!");
		    	}
		    	else
		    	{
		    		System.out.println("CLIENT IS STILL RUNNING....!!!");
		    	}
		    }
		    else
		    {	
		    	System.out.println("Please enter a valid command GET/PUT and Try again..!!!!");
		    }
		}
	 catch (IOException e) {
		// TODO Auto-generated catch block
		 System.err.println("Unable to estrablish the connection....!!! "+e.getMessage());
		//e.printStackTrace();
	}
    	catch(NullPointerException e)
    	{
    		System.out.println("Unable to connect to server");
    	}
		}
		else
		{
			System.out.println("Please enter a valid request in the below format and try again :"+"\n" + "Client Hostname port command filename");
		}
    	
	}
    	

	}


