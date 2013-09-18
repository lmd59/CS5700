/**
 * @author laurend
 *
 * main class to get flag via client and handle protocol parsing
 */
public class ClientRunner {
	//constants
	private static final int DEFAULT_PORT = 27993;
	private static final int DEFAULT_SSL_PORT = 27994;
	private static final String SPACE_STRING = " ";
	
	private static final String COURSE_HEADER = "cs5700fall2013";
	private static final String HELLO_FORMAT = 
			COURSE_HEADER + " HELLO %s";
	private static final String STATUS_FORMAT = 
			COURSE_HEADER + " STATUS %f %c %f";
	private static final String SOLUTION_FORMAT = 
			COURSE_HEADER + " %d";
	private static final String BYE_FORMAT = 
			COURSE_HEADER + " %s BYE";
	private static final String STATUS_TYPE_CHECK = "STATUS";
	private static final String BYE_TYPE_CHECK = "BYE";
	private static final String ADD_OP = "+";
	private static final String SUBTRACT_OP = "-";
	private static final String MULTIPLY_OP = "*";
	private static final String DIVIDE_OP = "/";
	private static final int FLAG_SIZE = 64;

	//TODO: remove
	private static String hostname = "cs5700f12.ccs.neu.edu";
	private static int port = 27993;
	private static String neuid = "001986230";//"000156594";
	private static boolean isSSL = false;
	
	/**
	 * @param args hostname and neuid and optionally port and ssl flag inputs
	 */
	public static void main(String[] args) {
		
		String secretFlag = null;
		
		//TODO: clean inputs
		
		//TODO: default port
		
		//TODO: default SSL
		
		SimpleClient client = new SimpleClient(port, isSSL, hostname);
		
		//open connection to server
		client.openConnection();
		
		//create hello message
		String helloMessage = String.format(HELLO_FORMAT, neuid);
		
		//write hello message
		client.writeMessage(helloMessage);
		
		while(secretFlag==null){
			
			//receive message
			String message = client.receiveMessage();
			
			//handle message (parse message type using simple check for contains type)
			if(message.contains(STATUS_TYPE_CHECK)){
				int solution = solveStatusExpression(message);
				//create solution message
				String solutionMessage = String.format(SOLUTION_FORMAT, solution);
				//write solution back to server
				client.writeMessage(solutionMessage);
				
			}else if(message.contains(BYE_TYPE_CHECK)){
				//get secret flag
				secretFlag = parseByeForFlag(message);
				//print secret flag to console
				System.out.println(secretFlag);
				
			}else{
				System.out.println("Did not receive STATUS or BYE message.");
				System.out.println("Message found was: " + message);
				System.out.println("Exiting...");
				System.exit(1);
			}
		}
		
		//finish closing client
		client.closeClient();
		

	}

	
	/**
	 * @param statusMessage a status message containing an arithmetic expression to be solved
	 * @return expression integer solution
	 * exits on bad status message received
	 */
	private static int solveStatusExpression(String statusMessage){
		
		//status message format is "[COURSE_HEADER] STATUS [num1] [operator] [num2]"
		double num1;
		double num2;
		String operator;
		double solutionDouble=0;//expression solution in double format
		
		//split message into fields using space as separator
		String[] splitString = statusMessage.split(SPACE_STRING);
		
		if(checkStatusFormat(splitString)){
			//get expression
			num1 = Double.parseDouble(splitString[2]);
			operator = splitString[3];
			num2 = Double.parseDouble(splitString[4]);
			
			//calculate solution
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
		
		//Note double solution is truncated instead of rounded to the 
		//nearest integer as truncation seems to be what the sever expects
		//original line was: return (int) Math.round(solutionDouble);
		return (int) solutionDouble;
	}
	
	/**
	 * @param fields a status message split into its fields
	 * @return true if status message is properly formatted
	 */
	private static boolean checkStatusFormat(String[] fields){
		//check number of fields
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
		try{
			Double.parseDouble(fields[4]);
		}catch(NumberFormatException e){
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param byeMessage bye type message containing a secret flag
	 * @return secret flag
	 * exits on bad bye message received
	 */
	private static String parseByeForFlag(String byeMessage){
		String flag=null;
		
		//split message into fields using space as separator
		String[] splitString = byeMessage.split(SPACE_STRING);
		if(checkByeFormat(splitString)){
			
			//flag is second field
			flag = splitString[1];
		}else{
			System.out.println("Incorrect BYE message format.");
			System.out.println("Exiting...");
			System.exit(1);
		}
		return flag;
	}
	
	/**
	 * @param fields a bye message split into its fields
	 * @return true if bye message is properly formatted
	 */
	private static boolean checkByeFormat(String[] fields){
		//check number of fields
		if(fields.length != 3) return false;
		
		//first field is course header
		if(!fields[0].equals(COURSE_HEADER)) return false;
		
		//second field is 64 byte string (UTF-8 bytes)
		if(fields[1].length()!=FLAG_SIZE) return false;
		
		//third field is bye type
		if(!fields[2].equals(BYE_TYPE_CHECK)) return false;
		
		return true;
	}
}
