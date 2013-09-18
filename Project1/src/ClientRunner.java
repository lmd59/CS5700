import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;


/**
 * @author laurend
 *
 */
public class ClientRunner {
	//constants
	private static final int DEFAULT_PORT = 27993;
	private static final int DEFAULT_SSL_PORT = 27994;
	private static final String SPACE_STRING = " ";
	private static final char LINEFEED = '\n';
	
	private static final String COURSE_HEADER = "cs5700fall2013";
	private static final String HELLO_FORMAT = 
			COURSE_HEADER + " HELLO %s";
	private static final String STATUS_FORMAT = 
			COURSE_HEADER + " STATUS %f %c %f\n";
	private static final String SOLUTION_FORMAT = 
			COURSE_HEADER + " %d";
	private static final String BYE_FORMAT = 
			COURSE_HEADER + " %s BYE\n";
	private static final String STATUS_TYPE_CHECK = "STATUS";
	private static final String BYE_TYPE_CHECK = "BYE";
	private static final String ADD_OP = "+";
	private static final String SUBTRACT_OP = "-";
	private static final String MULTIPLY_OP = "*";
	private static final String DIVIDE_OP = "/";
	private static final int FLAG_SIZE = 64;

	
	private static String hostname = "cs5700f12.ccs.neu.edu";
	private static int port = 27993;
	private static String neuid = "000156594";
	private static boolean isSSL = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String secretFlag = null;
		
		System.out.println("Num args: " + args.length);
		
		//TODO: clean inputs
		
		//TODO: default port
		
		//TODO: default SSL
		
		SimpleClient client = new SimpleClient(port, isSSL, hostname);
		
		//connect: open connection to server
		client.openConnection();
		
		//create hello message
		String helloMessage = String.format(HELLO_FORMAT, neuid);
		
		//write hello message
		client.writeMessage(helloMessage);
		
		while(secretFlag==null){
			
			//receive message  (TODO: method should check length?)
			String message = client.receiveMessage();
			System.out.println("message Received: " + message);
			
			//parse message type using simple check for contains type
			if(message.contains(STATUS_TYPE_CHECK)){
				int solution = solveStatusExpression(message);
				String solutionMessage = String.format(SOLUTION_FORMAT, solution);
				client.writeMessage(solutionMessage);
				
			}else if(message.contains(BYE_TYPE_CHECK)){
				secretFlag = parseByeForFlag(message);
				System.out.println(secretFlag);
				
			}else{
				System.out.println("Did not receive STATUS or BYE message.");
				System.out.println("Message found was: " + message);
				System.out.println("Exiting...");
				System.exit(1);
			}
		}
		
		//finish closing client
		try {
			client.closeClient();
		} catch (IOException e) {
			System.out.println("Unable to close simple client!");
			System.out.println("Exiting with stacktrace:");
			e.printStackTrace();
			System.exit(1);
		}

	}

	
	//exits on bad status message received
	private static int solveStatusExpression(String statusMessage){
		
		double num1;
		double num2;
		String operator;
		double solutionDouble=0;
		
		//parseString
		
		String[] splitString = statusMessage.split(SPACE_STRING);
		
		if(checkStatusFormat(splitString)){
			num1 = Double.parseDouble(splitString[2]);
			operator = splitString[3];
			num2 = Double.parseDouble(splitString[4]);
			
			if(operator.equals(ADD_OP)){
				solutionDouble = num1+num2;
			}else if(operator.equals(SUBTRACT_OP)){
				solutionDouble = num1-num2;
			}else if(operator.equals(MULTIPLY_OP)){
				solutionDouble = num1*num2;
			}else if(operator.equals(DIVIDE_OP)){
				solutionDouble = num1/num2;
			}
			
		}else{
			System.out.println("Incorrect STATUS message format.");
			System.out.println("Exiting...");
			System.exit(1);
		}

		System.out.println("statusMessage: " + statusMessage);
		System.out.println("solution: " + solutionDouble);
		//Note double solution is truncated instead of rounded to the 
		//nearest integer as truncation seems to be what the sever expects
		//original line was: return (int) Math.round(solutionDouble);
		return (int) solutionDouble;
	}
	
	private static boolean checkStatusFormat(String[] fields){
		//check number of fields
		
		System.out.println("fields: " + Arrays.toString(fields));
		if(fields.length != 5) return false;
		
		//first field is course header
		if(!fields[0].equals(COURSE_HEADER)) return false;
		
		//second field is status type
		if(!fields[1].equals(STATUS_TYPE_CHECK)) return false;
		
		//third field is number
		try{
			Double.parseDouble(fields[2]);
		}catch(NumberFormatException e){
			return false;
		}
		
		//fourth field is operator
		if(!fields[3].equals("+") 
				&& !fields[3].equals("-") 
				&& !fields[3].equals("*") 
				&& !fields[3].equals("/")) return false;
		
		//fifth field is number 
		//TODO: check ends with new line terminator
		String lastField = fields[4];
//		if(!lastField.endsWith("\n")) return false;
//		System.out.println("Check 5b");
		try{
			//truncate last character
			lastField.substring(0, lastField.length()-1);
			Double.parseDouble(lastField);
		}catch(NumberFormatException e){
			return false;
		}
		
		return true;
	}
	
	//exits on bad bye message received
	private static String parseByeForFlag(String byeMessage){
		String flag=null;
		
		String[] splitString = byeMessage.split(SPACE_STRING);
		if(checkByeFormat(splitString)){
			flag = splitString[1];
		}else{
			System.out.println("Incorrect BYE message format.");
			System.out.println("Exiting...");
			System.exit(1);
		}
		return flag;
	}
	
	private static boolean checkByeFormat(String[] fields){
		//check number of fields
		if(fields.length != 3) return false;
		
		//first field is course header
		if(!fields[0].equals(COURSE_HEADER)) return false;
		
		//second field is 64 byte string
		//Assuming UTF-8 bytes TODO: check system encoding
		if(fields[1].length()!=FLAG_SIZE) return false;
		
		//third field is status type
		if(!fields[2].equals(BYE_TYPE_CHECK+LINEFEED)) return false;
		
		
		return true;
	}
}
