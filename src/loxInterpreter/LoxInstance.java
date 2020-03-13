package loxInterpreter;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
	private LoxClass obj;
	
	private final Map<String, Object> fields = new HashMap<>();
	
	LoxInstance(LoxClass obj){
		this.obj = obj;
	}
	
	Object get(Token name) {
		if(fields.containsKey(name.lexeme))
			return fields.get(name.lexeme);
		
		LoxFunction method = (LoxFunction) obj.findMethod(name.lexeme);
		if(method != null) return method.bind(this);
		
		throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
	}
	
	void set(Token name, Object value) {
		fields.put(name.lexeme, value);
	}
	
	@Override
	public String toString() {
		return obj.name + "instance";
	}
}
