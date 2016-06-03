import java.io.File;
import java.util.Scanner;


/*
 * A class representing the VM files parser. 
 */
public class Parser {
	
	private static final String NO_COMMAND = "\\s*(//.*)?";
	private static final String SPACE_DELIM = "\\s+";
	private static final String COMMENT = "//.*";
	private static final String EMPTY_STR = "";
	private static final int COMMAND_TYPE_INDEX = 0;
	private static final int ARG1_INDEX = 1;
	private static final int ARG2_INDEX = 2;
	private static final String PUSH = "push", POP = "pop", LABEL = "label", IF_GOTO = "if-goto", GOTO = "goto", 
			FUNCTION = "function", CALL = "call", REUTURN = "return";	
	private Scanner scanner;
	private String nextCommandStr;
	private String[] commandDetails;
	private CommandType commandType;
	
	
	/**
	 * The constructor.
	 * @param file the input file.
	 */
	public Parser(File file) {
		try {
			this.scanner = new Scanner(file);
		} 
		catch (Exception e) {
			System.exit(1);
		}
		this.nextCommandStr = null;
	}
	
	/**
	 * Returns true iff the file has more commands.
	 * @return true iff the file has more commands.
	 */
	public boolean hasMoreCommands() {
		// if the function was already called
		if (this.nextCommandStr != null) {
			return true;
		}
		String line;
		while (this.scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (!line.matches(NO_COMMAND)) {
				this.nextCommandStr = line;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Advances to the next command in the file. Must call to 
	 * "hasMoreCommands" method and receive true before calling this method. 
	 */
	public void advance() {
		this.nextCommandStr = this.nextCommandStr.replaceFirst(COMMENT, EMPTY_STR);
		this.nextCommandStr = this.nextCommandStr.trim();
		this.commandDetails = this.nextCommandStr.split(SPACE_DELIM);
		
		switch(commandDetails[COMMAND_TYPE_INDEX]) {
			case PUSH:
				this.commandType = CommandType.C_PUSH;
				break;
			case POP:
				this.commandType = CommandType.C_POP;
				break;
			case LABEL:
				this.commandType = CommandType.C_LABEL;
				break;
			case IF_GOTO:
				this.commandType = CommandType.C_IF;
				break;
			case GOTO:
				this.commandType = CommandType.C_GOTO;
				break;
			case FUNCTION:
				this.commandType = CommandType.C_FUCTION;
				break;
			case CALL:
				this.commandType = CommandType.C_CALL;
				break;
			case REUTURN:
				this.commandType = CommandType.C_RETURN;
				break;
			default:
				this.commandType = CommandType.C_ARITHMETIC;
				break;
		}
		
		this.nextCommandStr = null;
	}
	
	/**
	 * Returns the type of the current command (A, C or L).
	 * @return The command's type
	 */
	public CommandType commandType() {
		return this.commandType;
	}
	
	/**
	 * Returns the first argument of the current command.
	 */
	public String arg1() {
		int index = this.commandType == CommandType.C_ARITHMETIC ? COMMAND_TYPE_INDEX : ARG1_INDEX;
		return this.commandDetails[index];
	}

	/**
	 * Returns the second argument of the current command.
	 */
	public int arg2() {
		return Integer.parseInt(this.commandDetails[ARG2_INDEX]);
	}
	
}
