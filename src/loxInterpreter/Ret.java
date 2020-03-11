package loxInterpreter;

public class Ret extends RuntimeException{
	final Object value;
	
	Ret(Object value){
		super(null, null, false, false);
		this.value = value;
	}
}
