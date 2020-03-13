package loxInterpreter;

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

public class AstPrinter implements Expr.Visitor<String>{

	
	String print(Expr expr) {
		return expr.accept(this);
	}
	
	@Override
	public String visitBinaryExpr(Expr.Binary expr) {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitGroupingExpr(Grouping expr) {
		return parenthesize("group", expr.expression);
	}

	@Override
	public String visitLiteralExpr(Literal expr) {
		if (expr.value == null) return "nil";
		return expr.value.toString();
	}

	@Override
	public String visitUnaryExpr(Unary expr) {
		return parenthesize(expr.operator.lexeme, expr.right);
	}
	
	private String parenthesize(String name, Expr...exprs) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("(").append(name);
		for(Expr expr: exprs) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");
		return builder.toString();
	}
	
	public static void main(String[] args) {
	    Expr expression = new Expr.Binary(                     
	            new Expr.Unary(                                    
	                new Token(TokenType.MINUS, "-", null, 1),      
	                new Expr.Literal(123)),                        
	            new Token(TokenType.STAR, "*", null, 1),           
	            new Expr.Grouping(                                 
	                new Expr.Literal(45.67)));

	        System.out.println(new AstPrinter().print(expression));
	}

	@Override
	public String visitVariableExpr(Variable expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitAssignExpr(Assign expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitLogicalExpr(Logical expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitCallExpr(Call expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitGetExpr(Get expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitSetExpr(Set expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitThisExpr(This expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitSeriesGetExpr(SeriesGet expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitSeriesSetExpr(SeriesSet expr) {
		// TODO Auto-generated method stub
		return null;
	}

}
