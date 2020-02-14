package Interpreter;



public class Token {
	
	final TokenType type;
	final String lexeme;
	final Object literal;
	final int line;
	
	Token(TokenType type, String lexeme, Object literal, int line){
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
	}
	
	//According to Mr. Reaser a string builder (or string buffer) is more efficient than appending
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append(type).append(" ").append(lexeme).append(" ").append(literal);
		sb.append(type).append(" ");
		return sb.toString();
	}

}
