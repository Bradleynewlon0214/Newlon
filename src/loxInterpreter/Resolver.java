package loxInterpreter;

import java.util.HashMap;
import java.util.List; 
import java.util.Map;
import java.util.Stack;

import loxInterpreter.Expr.Assign;
import loxInterpreter.Expr.Binary;
import loxInterpreter.Expr.Call;
import loxInterpreter.Expr.Grouping;
import loxInterpreter.Expr.Literal;
import loxInterpreter.Expr.Logical;
import loxInterpreter.Expr.Unary;
import loxInterpreter.Stmt.Expression;
import loxInterpreter.Stmt.Function;
import loxInterpreter.Stmt.If;
import loxInterpreter.Stmt.Print;
import loxInterpreter.Stmt.Return;


public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>{
	private final Interpreter interpreter;
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();
	
	Resolver(Interpreter interpreter){
		this.interpreter = interpreter;
	}
	
	
	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		beginScope();
		resolve(stmt.statements);
		endScope();
		return null;
	}
	
	@Override
	public Void visitLetStmt(Stmt.Let stmt) {
		declare(stmt.name);
		if(stmt.initializer != null) {
			resolve(stmt.initializer);
		}
		define(stmt.name);
		return null;
	}
	
	@Override
	public Void visitVariableExpr(Expr.Variable expr) {
		if(!scopes.isEmpty() &&
			scopes.peek().get(expr.name.lexeme) == Boolean.FALSE){
				Lox.error(expr.name, "Cannot read local variable in its own initializer");
		}
		resolveLocal(expr, expr.name);
		return null;
			
	}
	
	private void resolve(Expr expr) {
		expr.accept(this);
	}
	
	void resolve(List<Stmt> statements) {
		for(Stmt statement: statements) {
			resolve(statement);
		}
	}
	
	private void resolve(Stmt stmt) {
		stmt.accept(this);
	}
	
	private void resolveFunction(Stmt.Function function) {
		beginScope();
		for(Token param : function.params) {
			declare(param);
			define(param);
		}
		resolve(function.body);
		endScope();
	}
	
	
	private void beginScope() {
		scopes.push(new HashMap<String, Boolean>());
	}
	
	private void endScope() {
		scopes.pop(); 
	}
	
	private void declare(Token name) {
		if(scopes.isEmpty()) return;
		
		Map<String, Boolean> scope = scopes.peek();
		scope.put(name.lexeme, false);
	}
	
	private void define(Token name) {
		if(scopes.isEmpty()) return;
		scopes.peek().put(name.lexeme, true);
	}
	
	private void resolveLocal(Expr expr, Token name) {
		for(int i = scopes.size() - 1; i >= 0; i--) {
			if(scopes.get(i).containsKey(name.lexeme)) {
				interpreter.resolve(expr, scopes.size() - 1 - i);
				return;
			}
		}
	}


	@Override
	public Void visitExpressionStmt(Expression stmt) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitFunctionStmt(Function stmt) {
		declare(stmt.name);
		define(stmt.name);
		
		resolveFunction(stmt);
		return null;
	}


	@Override
	public Void visitIfStmt(If stmt) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitPrintStmt(Print stmt) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitReturnStmt(Return stmt) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitAssignExpr(Assign expr) {
		resolve(expr.value);
		resolveLocal(expr, expr.name);
		return null;
	}


	@Override
	public Void visitBinaryExpr(Binary expr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitCallExpr(Call expr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitGroupingExpr(Grouping expr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitLiteralExpr(Literal expr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitLogicalExpr(Logical expr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Void visitUnaryExpr(Unary expr) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
