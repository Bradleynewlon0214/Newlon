package loxInterpreter;

import java.util.List;
import java.util.Map;

import DataFrame.DataFrame;
import DataFrame.Series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import loxInterpreter.Expr.Assign;
import loxInterpreter.Expr.Binary;
import loxInterpreter.Expr.Call;
import loxInterpreter.Expr.Get;
import loxInterpreter.Expr.Grouping;
import loxInterpreter.Expr.Literal;
import loxInterpreter.Expr.Logical;
import loxInterpreter.Expr.SeriesGet;
import loxInterpreter.Expr.SeriesSet;
import loxInterpreter.Expr.Set;
import loxInterpreter.Expr.This;
import loxInterpreter.Expr.Unary;
import loxInterpreter.Expr.Variable;
import loxInterpreter.Stmt.Block;
import loxInterpreter.Stmt.Class;
import loxInterpreter.Stmt.Expression;
import loxInterpreter.Stmt.Function;
import loxInterpreter.Stmt.If;
import loxInterpreter.Stmt.Let;
import loxInterpreter.Stmt.Print;
import loxInterpreter.Stmt.Return;
import newlon.LinearModelResult;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{

	final Environment globals = new Environment();
	private Environment environment = globals;
	private final Map<Expr, Integer> locals = new HashMap<>();
	
	
	
	Interpreter(){
		globals.define("clock", new LoxCallable(){
			@Override
			public int arity() {return 0;}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return (double)System.currentTimeMillis() / 1000.0;
			}
			
			@Override
			public String toString() { return "<native fn>"; }
		});
		
		globals.define("mean", new LoxCallable() {

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				if (arguments.get(0) instanceof LoxSeries) {
					LoxSeries series = (LoxSeries) arguments.get(0);
					return series.mean();
				}
				throw new RuntimeError(null, "expect a series");
			}

			@Override
			public int arity() {
				return 1;
			}
			
		});
		
		globals.define("sum", new LoxCallable() {

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				if (arguments.get(0) instanceof LoxSeries) {
					LoxSeries series = (LoxSeries) arguments.get(0);
					return series.sum();
				}
				throw new RuntimeError(null, "expect a series");
			}

			@Override
			public int arity() {
				return 1;
			}
			
		});
		
		globals.define("variance", new LoxCallable() {

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				if (arguments.get(0) instanceof LoxSeries) {
					LoxSeries series = (LoxSeries) arguments.get(0);
					return series.variance();
				}
				throw new RuntimeError(null, "expect a series");
			}

			@Override
			public int arity() {
				return 1;
			}
			
		});
		
		
		globals.define("ols", new LoxCallable() {
			
			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				if(!(arguments.get(0) instanceof DataFrame))
					throw new RuntimeError(null, "Expect a dataframe object as first argument.");
				
				DataFrame df = (DataFrame)arguments.get(0);
				String formula = (String)arguments.get(1);
				String[] s = formula.split("=");
				String yName = s[0].strip();
				
				double[] y = df.get(yName).toArray();	
				
				
				String[] xNames = s[1].split("\\+");
				double[][] xs = new double[xNames.length][];
				for(int i = 0; i < xNames.length; i++) {
					xs[i] = df.get(xNames[i].strip()).toArray();
				}
				
				
//				dataframe df -> "C:\Users\bradley\Desktop\R\hw_data.csv"; let mod -> ols(df, "kwh = tmin + tmax + cldcvr + wndspd");
				return new LinearModelResult(yName, xNames, y, xs);
			}

			@Override
			public int arity() {
				return 2;
			}
		});
	}
	
	void interpret(List<Stmt> statements) {
		try {
			for(Stmt statement : statements) {
				execute(statement);
			}
		} catch(RuntimeError error) {
			Lox.runtimeError(error);
		}
	}
	
	private String stringify(Object object) {
		if(object == null) return "nil";
		
		if(object instanceof Double) {
			String text = object.toString();
			if(text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		return object.toString();
	}
	
	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);
		
		switch(expr.operator.type) {
			case GREATER:
				checkNumberOperands(expr.operator, left, right);
				return (double)left > (double)right;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left >= (double)right;
			case LESS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left < (double)left;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left <= (double)right;
			case BANG_EQUAL: return !isEqual(left, right);
			case EQUAL_EQUAL: return isEqual(left, right);
			case MINUS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left - (double)right;
			case STAR:
				checkNumberOperands(expr.operator, left, right);
				return (double)left * (double)right;
			case PLUS:
				if(left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				}
				
				if(left instanceof String && right instanceof String) {
					return (String)left + (String)right;
				}
				throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
			case SLASH:
				checkNumberOperands(expr.operator, left, right);
				return (double)left / (double)right;
		}
		
		return null;
	}
		
	private void checkNumberOperands(Token operator, Object left, Object right) {
		if(left instanceof Double && right instanceof Double) return;
		throw new RuntimeError(operator, "Operands must be numbers.");
	}
	
	private boolean isEqual(Object a, Object b) {
		//nil is only equal to nil
		if(a == null && b == null) return true;
		if(a == null) return false;
		return a.equals(b);
	}

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return evaluate(expr.expression);
	}

	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = evaluate(expr.right);
		
		switch(expr.operator.type) {
			case BANG:
				return !isTruthy(right);
			case MINUS:
				checkNumberOperand(expr.operator, right);
				return -(double)right;
		}
		
		return null;
	}
	
	private void checkNumberOperand(Token operator, Object operand) {
		if(operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a numbers");
	}
	
	private boolean isTruthy(Object object) {
		if(object == null) return false;
		if(object instanceof Boolean) return (boolean)object;
		return true;
	}
	
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}
	private void execute(Stmt stmt) {
		stmt.accept(this);
	}
	
	void resolve(Expr expr, int depth) {
		locals.put(expr, depth);
	}

	@Override
	public Void visitExpressionStmt(Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitPrintStmt(Print stmt) {
		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));
		return null;
	}

	@Override
	public Void visitLetStmt(Let stmt) {
		Object value = null;
		if(stmt.initializer != null) {
			value = evaluate(stmt.initializer);
		}
		environment.define(stmt.name.lexeme, value);
		return null;
	}

	@Override
	public Object visitVariableExpr(Variable expr) {
		return lookUpVariable(expr.name, expr);
	}

	private Object lookUpVariable(Token name, Expr expr) {
		Integer distance = locals.get(expr);
		if(distance != null) {
			return environment.getAt(distance, name.lexeme);
		} else {
			return globals.get(name);
		}
	}
	
	@Override
	public Object visitAssignExpr(Assign expr) {
		Object value = evaluate(expr.value);
		Integer distance = locals.get(expr);
		if(distance != null) {
			environment.assignAt(distance, expr.name, value);
		} else {
			globals.assign(expr.name, value);
		}
		return value;
	}

	@Override
	public Void visitBlockStmt(Block stmt) {
		executeBlock(stmt.statements, new Environment(environment));
		return null;
	}
	
	void executeBlock(List<Stmt> statements, Environment environment) {
		Environment previous = this.environment;
		
		try {
			this.environment = environment;
			
			for(Stmt statement : statements) {
				execute(statement);
			}
		} finally {
			this.environment = previous;
		}
	}

	@Override
	public Void visitIfStmt(If stmt) {
		if(isTruthy(evaluate(stmt.condition))) {
			execute(stmt.thenBranch);
		} else if(stmt.elseBranch != null) {
			execute(stmt.elseBranch);
		}
		return null;
	}

	@Override
	public Object visitLogicalExpr(Logical expr) {
		Object left = evaluate(expr.left);
		
		if(expr.operator.type == TokenType.OR) {
			if (isTruthy(left)) return left;
		} else
			if(!isTruthy(left)) return left;
		return evaluate(expr.right);
	}

	@Override
	public Object visitCallExpr(Call expr) {
		Object callee = evaluate(expr.callee);
		
		List<Object> arguments = new ArrayList<>();
		for(Expr argument : expr.arguments) {
			arguments.add(evaluate(argument));
		}
		
		if(!(callee instanceof LoxCallable))
			throw new RuntimeError(expr.paren, "Can only call functions.");
		
		LoxCallable function = (LoxCallable)callee;
		
//		if(arguments.size() != function.arity()) {
//			throw new RuntimeError(expr.paren, "Expected " +
//					function.arity() + " arguments, but got " +
//					arguments.size() + ".");
//		}
		
		return function.call(this, arguments);
	}

	@Override
	public Void visitFunctionStmt(Function stmt) {
		System.out.println(stmt.body);
		LoxFunction function = new LoxFunction(stmt, environment, false);
		environment.define(stmt.name.lexeme, function);
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
		if(stmt.value != null) value = evaluate(stmt.value);
		throw new Ret(value);
	}

	@Override
	public Void visitClassStmt(Class stmt) {
		environment.define(stmt.name.lexeme, null);

		Map<String, LoxFunction> methods = new HashMap<>();
		for(Stmt.Function method: stmt.methods) {
			LoxFunction function = new LoxFunction(method, environment, method.name.lexeme.equals("construct"));
			methods.put(method.name.lexeme, function);
		}
		LoxClass obj = new LoxClass(stmt.name.lexeme, methods);
		environment.assign(stmt.name, obj);
		return null;
	}

	@Override
	public Object visitGetExpr(Get expr) {
		Object object = evaluate(expr.object);
		if(object instanceof LoxInstance)
			return ((LoxInstance) object).get(expr.name);
		throw new RuntimeError(expr.name, "Only instances have properties.");
	}

	@Override
	public Object visitSetExpr(Set expr) {
		Object object = evaluate(expr.object);
		
		if(!(object instanceof LoxInstance))
			throw new RuntimeError(expr.name, "Only instances have fields");
		
		Object value = evaluate(expr.value);
		((LoxInstance)object).set(expr.name, value);
		return null;
	}

	@Override
	public Object visitThisExpr(This expr) {
		return lookUpVariable(expr.keyword, expr);
	}

	@Override
	public Void visitSeriesStmt(Stmt.Series stmt) {
		environment.define(stmt.name.lexeme, new LoxSeries(stmt.values));
		return null;
	}

	@Override
	public Object visitSeriesGetExpr(SeriesGet expr) {
		Object object = evaluate(expr.object);
		if(!(object instanceof LoxSeries))
			throw new RuntimeError(expr.index, "Only series can be subsetted.");
		Double d = (Double)expr.index.literal;
		Integer i = d.intValue();
		return ((LoxSeries)object).get(i);
	}

	@Override
	public Object visitSeriesSetExpr(SeriesSet expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitDataFrameStmt(Stmt.DataFrame stmt) {
		environment.define(stmt.name.lexeme, new DataFrame((String)stmt.param));
		return null;
	}

}










