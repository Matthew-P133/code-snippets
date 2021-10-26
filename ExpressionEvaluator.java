import java.util.Scanner;

/*  This program takes a (non-algebraic) mathematical expression and returns a numerical answer, respecting the standard order of operations, and handling brackets.
/   e.g "(2+1.51)*10" returns "35.1", whereas "2+1.51*30" returns "47.3".
/   More complicated expressions with multiple levels of nested brackets are also handled (see commented-out examples in the code).
/   I wrote this using only concepts encountered in the first month of my software development master's to practise algorithmic thinking and string manipulation, 
/   (and to solve the MathEvaluator 'kata' on CodeWars). I am sure that there are better ways of solving this problem using data structures, and may re-visit this  /   code when I have more experience of data structures.
*/

public class ExpressionEvaluator {
	public static void main(String[] args) {
		
		// set expression to be evaluated
		String expression = "(1-2)+-(--(-(-4)))";
    
    // other example expressions
		//expression = "2 /2+3 * 4.75- -6";
		//expression = "1 - -(-(--(-4)))";
		//expression = "(1 - 2) + -(-(-(-4)))";
		//expression = "(123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25)) / 20) - (123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25)) / 20) + (13 - 2)/ -(-11)";
	
		// format expression and provide output
		expression = expression.replaceAll("\\s",  "");
		System.out.printf("%-30s %s\n", "Expression to evaluate:", expression);

		// evaluate and print answer
		String simplifiedExpression = debracket(expression);
		String finalAnswer = evaluate(simplifiedExpression);
		System.out.printf("%-30s %s", "Final answer:", finalAnswer);
	}

	static String debracket(String expression) {

		String bracketContent;
		int closingBracket;
		int lastOpenBracket;

		// if brackets present remove last set
		if (expression.lastIndexOf("(") != -1) {
			lastOpenBracket = expression.lastIndexOf("(");
			closingBracket = expression.indexOf(")", lastOpenBracket);
			bracketContent = expression.substring(lastOpenBracket + 1, closingBracket);

			// if no further brackets within this nest, evaluate contents and replace in expression
			if (debracket(bracketContent).equals(bracketContent)) {
				bracketContent = bracketContent.replace("--", "+");
				bracketContent = evaluate(bracketContent);
			}

			// update expression
			String newExpression = expression.substring(0, lastOpenBracket) + bracketContent
									+ expression.substring(closingBracket + 1, expression.length());
			System.out.printf("%-30s %s\n", "Simplifying...", newExpression);

			// recursively remove remaining brackets
			return debracket(newExpression);

		} 
		// when all brackets removed return final fully simplified expression
		else {
			return expression;
		}
	}
	
	
	static String evaluate(String simpleExpression) {
		
		// handle double negative sign etc.
		simpleExpression = simpleExpression.replaceAll("---", "-");
		simpleExpression = simpleExpression.replaceAll("--", "+");
		simpleExpression = simpleExpression.replaceAll("\\+-", "-");
		
		String partiallyEvalulated = simpleExpression;
		
		// evalulate / or * recursively until no longer present then evaluate + - recursively until no longer present then return answer
		if (simpleExpression.indexOf("/") != -1) {
			// find operator and split expression
			int div = simpleExpression.indexOf("/");
			String beforeOperation = simpleExpression.substring(0, div);
			String afterOperation = simpleExpression.substring(div+1);
			Scanner before = new Scanner(beforeOperation);
			Scanner after = new Scanner(afterOperation);
			
			// find operands
			String[] operands = operandFinder(before, after);
			Double result;
			String firstPart = "";
			
			// evaluate result of operation and form new expression
			if (operands[0].equals("i")) {	// if no leading operand (e.g. "-10")
				result = Double.parseDouble(operands[1]);
				firstPart = "";
			} else {
				result = Double.parseDouble(operands[0]) / Double.parseDouble(operands[1]);
				firstPart = beforeOperation.substring(0, beforeOperation.lastIndexOf(operands[0]));
			}
			String lastPart = simpleExpression.substring(div+1 + operands[1].length());
			partiallyEvalated = firstPart + result.toString() + lastPart;
			
			return evaluate(partiallyEvaluated);
		}
		else if (simpleExpression.indexOf("*") != -1) {
			int div = simpleExpression.indexOf("*");
			String beforeOperation = simpleExpression.substring(0, div);
			String afterOperation = simpleExpression.substring(div+1);
			Scanner before = new Scanner(beforeOperation);
			Scanner after = new Scanner(afterOperation);

			String[] operands = operandFinder(before, after);
			Double result;
			String firstPart = "";
			
			if (operands[0].equals("i")) {
				result = Double.parseDouble(operands[1]);
				firstPart = "";
			} else {
				result = Double.parseDouble(operands[0]) * Double.parseDouble(operands[1]);
				firstPart = beforeOperation.substring(0, beforeOperation.lastIndexOf(operands[0]));
			}
			String lastPart = simpleExpression.substring(div+1 + operands[1].length());
			partiallyEvaluated = firstPart + result.toString() + lastPart;
			
			return evaluate(partiallyEvaluated);
		}
		
		else if (simpleExpression.indexOf("-") != -1 && simpleExpression.indexOf("-") != 0) {
			int div = simpleExpression.indexOf("-");
			String beforeOperation = simpleExpression.substring(0, div);
			String afterOperation = simpleExpression.substring(div+1);
			Scanner before = new Scanner(beforeOperation);
			Scanner after = new Scanner(afterOperation);
			
			String[] operands = operandFinder(before, after);
			Double result;
			String firstPart = "";
			
			if (operands[0].equals("i")) {
				result = Double.parseDouble(operands[1]);
				firstPart = "";
			} else {
				result = Double.parseDouble(operands[0]) - Double.parseDouble(operands[1]);
				firstPart = beforeOperation.substring(0, beforeOperation.lastIndexOf(operands[0]));
			}

			String lastPart = simpleExpression.substring(div+1 + operands[1].length());
			partiallyEvaluated = firstPart + result.toString() + lastPart;
			
			return evaluate(partiallyEvaluated);
		}
	
		else if (simpleExpression.indexOf("+") != -1) {
			int div = simpleExpression.indexOf("+");
			String beforeOperation = simpleExpression.substring(0, div);
			String afterOperation = simpleExpression.substring(div+1);
			
			Scanner before = new Scanner(beforeOperation);
			Scanner after = new Scanner(afterOperation);
			
			String[] operands = operandFinder(before, after);
			Double result;
			String firstPart = "";
			
			if (operands[0].equals("i")) {
				result = Double.parseDouble(operands[1]);
				firstPart = "";
			} else {
				result = Double.parseDouble(operands[0]) + Double.parseDouble(operands[1]);
				firstPart = beforeOperation.substring(0, beforeOperation.lastIndexOf(operands[0]));
			}
			String lastPart = simpleExpression.substring(div+1 + operands[1].length());
			partiallyEvaluated = firstPart + result.toString() + lastPart;

			return evaluate(partiallyEvaluated);
		}
		else {
			return partiallyEvaluated;
		}
	}
	
	
	static String[] operandFinder(Scanner before, Scanner after) {
		String[] operands = new String[2];
		String operandOne = "i";	// value of "i" persisting indicates no leading operand
		String operandTwo = "";
		boolean operandFound = false;
		String temp = "";
		
		try {	// handle case of no leading operand
			temp = before.next();
		}
		catch (Exception e) {
			operandFound = true;
		}
		
		while (!operandFound) {
			try {
				Double.parseDouble(temp);
				operandOne = temp;
				if (operandOne.charAt(0) == '+') {
					operandOne = operandOne.replace("+", "");
				}
				operandFound = true;
			} catch (Exception e) {}	
			temp = temp.substring(1, temp.length());
		}

		temp = after.next();
		operandFound = false;
		while (!operandFound) {
			try {
				Double.parseDouble(temp);
				operandTwo = temp;
				operandFound = true;
			} catch (Exception e) {
			}
			temp = temp.substring(0, temp.length() - 1);
		}
		
		operands[0] = operandOne;
		operands[1] = operandTwo;
		return operands;
	}
}
