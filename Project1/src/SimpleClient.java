import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


/**
 * @author laurend
 * 
 * client to send and receive messages to/from a server with a simple socket or ssl socket
 *
 */
public class SimpleClient {
	//TODO: update makefile

	//fields
	//inputs
	private int port;
	private boolean isSSL;
	private String hostname;
	//socket
	private Socket connection;
	//streams
	private InputStream is;
	private OutputStream os;
	//reader and writer
	private BufferedReader reader;
	private PrintWriter writer;
	
	
	
	/**
	 * @param port port number to connect to on server side
	 * @param isSSL true if the connection should be an SSL connection
	 * @param hostname host name of server to connect to
	 */
	public SimpleClient(int port, boolean isSSL, String hostname) {
		super();
		this.port = port;
		this.isSSL = isSSL;
		this.hostname = hostname;
	}
	
	/**
	 * creates a socket connection to this client's specified server hostname, 
	 * port and ssl setting
	 */
	public void openConnection(){
		
		try {
			//create socket connection
			if(isSSL){
				//Create TrustManager that trusts all certs
				TrustAllManager trustAll = new TrustAllManager();
				
				//Add to SSL context with nonspecific keymanagers and secure random
				SSLContext ctx = SSLContext.getInstance("SSL");
				ctx.init(null, new TrustManager[]{trustAll}, new SecureRandom());
				
				//create ssl socket
			    SSLSocketFactory socketFactory = ctx.getSocketFactory();
	            connection = socketFactory.createSocket(hostname, port);
		        
			}else{
				//create simple socket
				connection = new Socket(hostname, port);
			}
			
			//set 60 second timeout
			connection.setSoTimeout(60000);
			
			//create streams
			is = connection.getInputStream();
			os = connection.getOutputStream();
			
			//create easy io access
			reader = new BufferedReader(new InputStreamReader(is));
			writer = new PrintWriter(new OutputStreamWriter(os),true);
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Cannot get SSL context.");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		} catch (KeyManagementException e) {
			System.out.println("Unable to initialize SSL context.");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);	
		} catch (UnknownHostException e) {
			System.out.println("Could not find host.");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("IOException on create easy io access.");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/**
	 * @param msg one line message to write to socket connection 
	 * (written with line separator)
	 */
	public void writeMessage(String msg){
		//write
		writer.println(msg);
	}
	
	/**
	 * @return one line message received from socket connection
	 */
	public String receiveMessage(){
		
		String message = null;
		try {
			//read
			message = reader.readLine();
			
			//check message validity
			if(message==null){
				System.out.println("Message is null.");
				System.out.println("Exiting...");
				System.exit(1);
			}
			if(message.length()>256 || message.length()<=0){
				System.out.println("Message is inappropriate length.");
				System.out.println("Exiting...");
				System.exit(1);
			}
			
		} catch (IOException e) {
			System.out.println("IOException on read.");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		} catch (IndexOutOfBoundsException e){
			System.out.println("Too many characters read before eos- message too long");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		} 
		return message;
	}
	
	/**
	 * closes all necessary underlying state of simple client
	 */
	public void closeClient(){
		try{
			reader.close();
			writer.close();
			is.close();
			os.close();
			connection.close();
		} catch (IOException e) {
			System.out.println("Unable to close simple client!");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		}
	}
		
}
