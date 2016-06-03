import java.io.File;
import java.io.FileFilter;


/**
 * The main class of the program.
 */
public class VMtranslator {
	
	static final String VM_SUFF = ".vm";
	private static final String ASM_SUFF = ".asm";
	private static final int VALID_ARGS_NUM = 1;
	private static final int FAILURE = -1;
	private static final String ARGS_NUM_ERR_MSG = "Usage: VMtranslator: <file name or directory>.";
	private static final String FILE_ERR_MSG = "File doesn't exists.";
	private static final String VM_FILE_REGEX = (".*\\.vm");
	private static final String FILE_TYPE_ERR_MSG = "Program receives .vm files.";
	private static final String ARG_TYPE_ERR_MSG = "Program receives .vm file or directory.";
	
	
	/*
	 * Translates a .vm file, and writes the output using the codeWriter.
	 */
	private static void translateVMfile(File file, CodeWriter codeWriter) {
		codeWriter.setFileName(file.getName());
		Parser parser = new Parser(file);
		while (parser.hasMoreCommands()) {
			parser.advance();
			switch (parser.commandType()) {
			case C_ARITHMETIC:
				codeWriter.writeArithmetic(parser.arg1());
				break;
			case C_PUSH:
			case C_POP:
				codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
				break;
			case C_LABEL:
				codeWriter.writeLabel(parser.arg1());
				break;
			case C_IF:
				codeWriter.writeIf(parser.arg1());
				break;
			case C_GOTO:
				codeWriter.writeGoto(parser.arg1());
				break;
			case C_CALL:
				codeWriter.writeCall(parser.arg1(), parser.arg2());
				break;
			case C_FUCTION:
				codeWriter.writeFunction(parser.arg1(), parser.arg2());
				break;
			case C_RETURN:
				codeWriter.writeReturn();
				break;
			default:
				System.exit(1);		// shouldn't reach here
			}
		}
	}
	
	/*
	 * The main method of the program.
	 */
	public static void main(String args[]) {
		if (args.length != VALID_ARGS_NUM) {
			System.err.println(ARGS_NUM_ERR_MSG);
			System.exit(1);
		}
		
		File file = new File(args[0]);
		
		if (!file.exists()) {
			System.err.println(FILE_ERR_MSG);
			System.exit(1);
		}
		
		CodeWriter codeWriter = null;	
		if (file.isFile()) {
			String fileName = file.getAbsolutePath();
			if (fileName.matches(VM_FILE_REGEX)) {
				int lastIndexOf = fileName.lastIndexOf(VM_SUFF);
				if (lastIndexOf != FAILURE) {
					fileName = fileName.substring(0, lastIndexOf);	
				}
				fileName += ASM_SUFF;
				codeWriter = new CodeWriter(fileName);
				translateVMfile(file, codeWriter);
			} else {
				System.err.println(FILE_TYPE_ERR_MSG);
				System.exit(1);
			}
		} else if (file.isDirectory()) {
			// creates new file name filter which accepts .vm files.
			FileFilter vmFileFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().matches(VM_FILE_REGEX);
				}
			};
			
			String fileName = file.getAbsolutePath() + "/" + file.getName() + ASM_SUFF;
			codeWriter = new CodeWriter(fileName);
			for (File vmFile : file.listFiles(vmFileFilter)) {
				if (vmFile.isFile()) {
					translateVMfile(vmFile, codeWriter);
				}
			}
		} else {
			System.err.println(ARG_TYPE_ERR_MSG);
			System.exit(1);
		}
		codeWriter.close();
	}
	
}
