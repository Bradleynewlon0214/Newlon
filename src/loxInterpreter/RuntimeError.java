package loxInterpreter;

public class RuntimeError extends RuntimeException {

	final Token token;
	
	RuntimeError(Token token, String message){
		super(message + token);
		this.token = token;
	}
	
}
