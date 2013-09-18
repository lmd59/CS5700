import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;


public class SimpleClient {
	//TODO: finish commenting,
	//get rid of excess TODOs, get rid of excess printlns

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
	//writers
	private BufferedReader reader;
	private PrintWriter writer;
	
	
	
	public SimpleClient(int port, boolean isSSL, String hostname) {
		super();
		this.port = port;
		this.isSSL = isSSL;
		this.hostname = hostname;
	}
	
	public void openConnection(){
		System.out.println("opening connection");
		
		try {
			//create socket connection
			if(isSSL){
				SocketFactory socketFactory = SSLSocketFactory.getDefault();
	            connection = socketFactory.createSocket(hostname, port);
			}else{
				connection = new Socket(hostname, port);
			}
			
			//create streams
			is = connection.getInputStream();
			os = connection.getOutputStream();
			
			//create easy io access
			reader = new BufferedReader(new InputStreamReader(is));
			writer = new PrintWriter(new OutputStreamWriter(os),true);
			
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
	
	public void writeMessage(String msg){
		
		System.out.println("Test connection: " + connection.isConnected());;
		System.out.println("writing message: " + msg);
		writer.println(msg);
	}
	
	public String receiveMessage(){
		System.out.println("receiving message");
		//TODO: check timing problem
		//TODO: check for null return
		//TODO: add timeout
		String message = null;
		try {
			
			System.out.println("Reader ready");//TODO: remove
			message = reader.readLine()+ "\n";
			
//			char[] charBuf = new char[256];
//			int numCharsRead = reader.read(charBuf);//TODO: for error checking?
//			message = new String(charBuf);//TODO: check null values are left off
//			System.out.println("message received: " + message);//TODO: remove
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
	
	public void closeClient() throws IOException{
		System.out.println("closing client");
			reader.close();
			writer.close();
			is.close();
			os.close();
			connection.close();
	}
		
}
