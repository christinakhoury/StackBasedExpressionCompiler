import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Original Parser - Evaluates expressions directly (alternative to AST approach)
 * This parser evaluates expressions immediately rather than building an AST
 * It maintains variable state and returns final variable values
*/
public class Parser {
    private final List<Token> tokens;   // List of tokens from tokenizer
    private int currentTokenIndex = 0;  // Current position in token list
    private final Map<String, Double> variables = new HashMap<>();  // Variable storage

    /**
     * Constructor - initializes with tokens from tokenizer
     * @param tokens List of tokens to parse
    */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Main parsing method - processes all statements and returns variable values
     * @return Map of variable names to their final values
    */
    public Map<String, Double> parse() {
    	// Process all tokens until end of input
        while (!isAtEnd()) {
            Token current = peek();
            
            // Skip empty semicolons (statement separators)
            if (current.type == Token.Type.SEMICOLON) {
                advance();
                continue;
            }
            
            // Check for assignment statements: variable = expression
            if (current.type == Token.Type.IDENTIFIER && lookahead().type == Token.Type.ASSIGNMENT) {
            	// Parse assignment: extract variable name
                String varName = current.value;
                
                advance(); // consume identifier token
                advance(); // consume '=' token
                
                // Evaluate the expression and store result in variable
                double value = evaluateExpression();
                variables.put(varName, value);
                
                // optional semicolon at end of statement
                if (peek().type == Token.Type.SEMICOLON) advance();
            } else {
            	
            	// Handle expression statements (without assignment)
                // Evaluate expression but discard result (could print it)
                // either an expression (ignore result) or a stray token -> try to evaluate
                double val = evaluateExpression();
                if (peek().type == Token.Type.SEMICOLON) advance();
            }
        }
        return variables; // Return all variable assignments
    }
    
    /**
     * Entry point for expression evaluation
     * Handles addition and subtraction (lowest precedence)
     * Grammar: expression → term ( ('+' | '-') term )*
     * @return Result of the expression
    */
    private double evaluateExpression() {
        double left = parseTerm();          // Evaluate first term
        
        // Handle consecutive + and - operators (left-associative)
        while (matchOperator("+", "-")) {
            String op = previous().value;   // Get the operator
            double right = parseTerm();     // Evaluate next term
            
            // Apply the operation
            if (op.equals("+")) left = left + right;
            else left = left - right;
        }
        return left;
    }
    
    /**
     * Handles multiplication, division, and modulus
     * Grammar: term → factor ( ('*' | '/' | '%') factor )*
     * @return Result of the term
    */
    private double parseTerm() {
        double left = parseFactor(); // Evaluate first factor
        
        // Handle consecutive *, /, % operators (left-associative)
        while (matchOperator("*", "/", "%")) {
            String op = previous().value;  // Get the operator
            double right = parseFactor();  // Evaluate next factor
            
            // Apply the operation with error checking for division by zero
            if (op.equals("*")) left = left * right;
            else if (op.equals("/")) {
                if (right == 0) {
                    System.out.println("Error: Division by zero at pos " + previous().pos);
                    return Double.NaN; // Return Not-a-Number for division by zero
                }
                left = left / right;
            } else if (op.equals("%")) left = left % right; // Modulus operation
        }
        return left;
    }

    /**
     * Handles unary operators, numbers, variables, parentheses, and exponentiation
     * Grammar: factor → ('-') factor | number | identifier | '(' expression ')'
     * Also handles right-associative exponentiation
     * @return Result of the factor
    */
    private double parseFactor() {
    	// Handle unary minus: -expression
        if (matchOperator("-")) {
            double v = parseFactor(); // Recursively parse the operand
            return -v;  // Apply negation
        }

        double value;
        Token tok = peek();  // Look at current token

        // Handle different types of primary expressions
        if (tok.type == Token.Type.NUMBER) {
        	// Numeric literal: convert string to double
            value = Double.parseDouble(tok.value);
            advance(); // Consume the number token
            
        } else if (tok.type == Token.Type.IDENTIFIER) {
        	// Variable reference: look up value in variable map
            value = retrieveVariableValue(tok.value, tok.pos);
            advance(); // Consume the identifier token
            
        } else if (tok.type == Token.Type.LPAREN) {
        	// Parenthesized expression: ( expression )
            advance(); // Consume '('
            value = evaluateExpression(); // Evaluate expression inside parentheses
            
            // Expect closing parenthesis
            if (peek().type != Token.Type.RPAREN) {
                System.out.println("Error: Expected ')' at pos " + peek().pos);
            } else {
                advance(); // Consume ')'
            }
        } else {
        	// Unexpected token - report error and skip
            System.out.println("Error: Unexpected token '" + tok.value + "' at pos " + tok.pos);
            advance();
            return Double.NaN; // Return error value
        }

        // handle right-associative exponentiation ^ and **
        while (matchOperator("^", "**")) {
            String op = previous().value;
            double exponent = parseFactor();   // Note: recursive call makes it right-associative
            value = Math.pow(value, exponent); // Apply exponentiation
        }

        return value;
    }

    /**
     * Retrieves variable value from storage, with error handling for undefined variables
     * @param name Variable name to look up
     * @param pos Position in source for error reporting
     * @return Variable value (0.0 if undefined)
    */
    private double retrieveVariableValue(String name, int pos) {
        if (!variables.containsKey(name)) {
        	// Variable not defined - warn and return default value
            System.out.println("Warning: variable '" + name + "' not defined at pos " + pos + ", defaulting to 0.0");
            return 0.0;
        }
        return variables.get(name); // Return stored value
    }

    // --------- PARSER UTILITY METHODS ------------
    
    /**
     * Checks if current token matches any of the given operators and consumes it if so
     * @param ops Operators to match against
     * @return true if matched and consumed, false otherwise
    */
    private boolean matchOperator(String... ops) {
        Token t = peek();
        if (t.type != Token.Type.OPERATOR) return false; // Not an operator token
        
        // Check if token value matches any of the provided operators
        for (String op: ops) {
        	if (t.value.equals(op)) { 
        		advance(); // Consume the token
        		return true; 
        	}
        }
        return false;
    }

    /**
     * @return The most recently consumed token
    */
    private Token previous() {
        return tokens.get(currentTokenIndex - 1);
    }

    /**
     * @return The current token without consuming it
    */
    private Token peek() {
        if (currentTokenIndex >= tokens.size()) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(currentTokenIndex);
    }

    /**
     * @return The next token without consuming it (lookahead)
    */
    private Token lookahead() {
        if (currentTokenIndex + 1 >= tokens.size()) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(currentTokenIndex + 1);
    }

    /**
     * Advances to the next token
    */
    private void advance() {
        if (!isAtEnd()) currentTokenIndex++;
    }

    /**
     * @return true if no more tokens to process
    */
    private boolean isAtEnd() {
        return peek().type == Token.Type.EOF;
    }
}
