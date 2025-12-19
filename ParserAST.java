import java.util.*;

/**
 * Parser that builds an Abstract Syntax Tree (AST) from tokens using recursive descent.
 * This parser implements the grammar rules and constructs a structured representation
 * of the source code that can be used for code generation and analysis.
 */
public class ParserAST {
    private final List<Token> tokens;  // List of tokens from the tokenizer
    private int pos = 0;               // Current position in the token list

    /**
     * Constructor - initializes the parser with tokens
     * @param tokens List of tokens to parse
     */
    public ParserAST(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses a sequence of statements until end of input
     * @return List of AST nodes representing all statements in the program
     */
    public List<ASTNode> parseStatements() {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            // Skip empty semicolons (extra statement separators)
            if (peek().type == Token.Type.SEMICOLON) { 
                pos++; 
                continue; 
            }
            try {
                // Parse each statement and add to the list
                statements.add(parseStatement());
            } catch (ParseException e) {
                // Error recovery: report error and synchronize to next statement
                System.err.println("Parse error: " + e.getMessage());
                synchronize();
            }
        }
        return statements;
    }

    /**
     * Parses a single statement (assignment or control flow)
     * @return AST node representing the statement
     */
    private ASTNode parseStatement() {
        // Check for if statements first (they start with 'if' keyword)
        if (peek().type == Token.Type.IF) {
            return parseIfStatement();
        }
        
        // Regular assignment statement: variable = expression
        Token id = expect(Token.Type.IDENTIFIER, "Expected variable name");
        expect(Token.Type.ASSIGNMENT, "Expected '=' after variable name");
        ASTNode expr = parseExpression();
        // Optional semicolon at end of statement
        if (peek().type == Token.Type.SEMICOLON) pos++;
        return new AssignNode(id.value, expr);
    }

    /**
     * Parses an if statement with optional else clause
     * Grammar: if ( condition ) statement [else statement]
     * @return IfNode representing the conditional statement
     */
    private ASTNode parseIfStatement() {
        expect(Token.Type.IF, "Expected 'if'");
        expect(Token.Type.LPAREN, "Expected '(' after 'if'");
        ASTNode condition = parseExpression(); // Parse the condition inside parentheses
        expect(Token.Type.RPAREN, "Expected ')' after condition");
        
        // Parse the then branch (statement to execute if condition is true)
        ASTNode thenBranch = parseStatement();
        ASTNode elseBranch = null;
        
        // Check for optional else clause
        if (match(Token.Type.ELSE)) {
            elseBranch = parseStatement();
        }
        
        return new IfNode(condition, thenBranch, elseBranch);
    }

    /**
     * Entry point for expression parsing
     * @return AST node representing the expression
     */
    private ASTNode parseExpression() {
        return parseLogicalOr(); // Start with lowest precedence operator
    }

    /**
     * Parses logical OR operations (||) - lowest precedence
     * Grammar: logicalOr → logicalAnd ( '||' logicalAnd )*
     * @return AST node representing the logical OR expression
     */
    private ASTNode parseLogicalOr() {
        ASTNode node = parseLogicalAnd(); // Parse first operand
        // Handle consecutive OR operators (left-associative)
        while (matchOperator("||")) {
            String op = previous().value;
            ASTNode right = parseLogicalAnd(); // Parse next operand
            node = new BinaryOpNode(op, node, right); // Build AST node
        }
        return node;
    }

    /**
     * Parses logical AND operations (&&)
     * Grammar: logicalAnd → comparison ( '&&' comparison )*
     * @return AST node representing the logical AND expression
     */
    private ASTNode parseLogicalAnd() {
        ASTNode node = parseComparison(); // Parse first operand
        // Handle consecutive AND operators (left-associative)
        while (matchOperator("&&")) {
            String op = previous().value;
            ASTNode right = parseComparison(); // Parse next operand
            node = new BinaryOpNode(op, node, right); // Build AST node
        }
        return node;
    }

    /**
     * Parses comparison operations (==, !=, <, <=, >, >=)
     * Grammar: comparison → term ( ('==' | '!=' | '<' | '<=' | '>' | '>=' ) term )*
     * @return AST node representing the comparison expression
     */
    private ASTNode parseComparison() {
        ASTNode node = parseTerm(); // Parse first operand
        // Handle consecutive comparison operators (left-associative)
        while (matchOperator("==", "!=", "<", "<=", ">", ">=")) {
            String op = previous().value;
            ASTNode right = parseTerm(); // Parse next operand
            node = new BinaryOpNode(op, node, right); // Build AST node
        }
        return node;
    }

    /**
     * Parses addition and subtraction operations (+, -)
     * Grammar: term → factor ( ('+' | '-') factor )*
     * @return AST node representing the term expression
     */
    private ASTNode parseTerm() {
        ASTNode node = parseFactor(); // Parse first operand
        // Handle consecutive + and - operators (left-associative)
        while (matchOperator("+", "-")) {
            String op = previous().value;
            ASTNode right = parseFactor(); // Parse next operand
            node = new BinaryOpNode(op, node, right); // Build AST node
        }
        return node;
    }

    /**
     * Parses multiplication, division, and modulus operations (*, /, %)
     * Grammar: factor → unary ( ('*' | '/' | '%') unary )*
     * @return AST node representing the factor expression
     */
    private ASTNode parseFactor() {
        ASTNode node = parseUnary(); // Parse first operand
        // Handle consecutive *, /, % operators (left-associative)
        while (matchOperator("*", "/", "%")) {
            String op = previous().value;
            ASTNode right = parseUnary(); // Parse next operand
            node = new BinaryOpNode(op, node, right); // Build AST node
        }
        return node;
    }

    /**
     * Parses unary operations (-, !)
     * Grammar: unary → ('-' | '!') unary | exponent
     * @return AST node representing the unary expression
     */
    private ASTNode parseUnary() {
        // Check for unary operators at the beginning
        if (matchOperator("-", "!")) {
            String op = previous().value;
            ASTNode expr = parseUnary(); // Recursively parse the operand
            return new UnaryOpNode(op, expr); // Build unary operation node
        }
        return parseExponent(); // No unary operator, move to next precedence level
    }

    /**
     * Parses exponentiation operations (^, **) - right-associative
     * Grammar: exponent → primary ( ('^' | '**') primary )*
     * @return AST node representing the exponentiation expression
     */
    private ASTNode parseExponent() {
        ASTNode node = parsePrimary(); // Parse base (left operand)
        // Handle consecutive exponentiation operators (right-associative)
        while (matchOperator("^", "**")) {
            String op = previous().value;
            ASTNode right = parsePrimary(); // Parse exponent (right operand)
            node = new BinaryOpNode(op, node, right); // Build AST node
        }
        return node;
    }

    /**
     * Parses primary expressions - the basic building blocks
     * Grammar: primary → number | identifier | boolean | '(' expression ')'
     * @return AST node for the primary expression
     */
    private ASTNode parsePrimary() {
        Token t = peek(); // Look at current token without consuming it
        
        // Handle numeric literals
        if (match(Token.Type.NUMBER)) return new NumberNode(Double.parseDouble(t.value));
        // Handle variable identifiers
        if (match(Token.Type.IDENTIFIER)) return new IdentifierNode(t.value);
        // Handle boolean literals (true/false)
        if (match(Token.Type.BOOLEAN)) return new BooleanNode(Boolean.parseBoolean(t.value));
        // Handle parenthesized expressions
        if (match(Token.Type.LPAREN)) {
            ASTNode expr = parseExpression(); // Parse expression inside parentheses
            expect(Token.Type.RPAREN, "Expected ')' after expression");
            return expr;
        }
        
        // If none of the above matched, it's a syntax error
        throw new ParseException("Unexpected token: " + t.value, t.line, t.column);
    }

    // ========== PARSER UTILITY METHODS ==========

    /**
     * Returns the current token without consuming it
     * @return Current token, or EOF if at end of input
     */
    private Token peek() {
        if (pos >= tokens.size()) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(pos);
    }

    /**
     * Returns the most recently consumed token
     * @return Previous token, or EOF if at beginning
     */
    private Token previous() { 
        if (pos == 0) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(pos - 1); 
    }

    /**
     * Checks if current token matches the given type and consumes it if so
     * @param type Token type to match against
     * @return true if token matched and was consumed, false otherwise
     */
    private boolean match(Token.Type type) {
        if (peek().type == type) { pos++; return true; }
        return false;
    }

    /**
     * Checks if current token matches any of the given operators and consumes it if so
     * @param ops Operator values to match against
     * @return true if operator matched and was consumed, false otherwise
     */
    private boolean matchOperator(String... ops) {
        Token t = peek();
        if (t.type != Token.Type.OPERATOR) return false;
        for (String op : ops) if (t.value.equals(op)) { pos++; return true; }
        return false;
    }

    /**
     * Expects a token of the given type, throws parse error if not found
     * @param type Expected token type
     * @param errorMessage Error message to display if expectation fails
     * @return The consumed token
     * @throws ParseException if expected token not found
     */
    private Token expect(Token.Type type, String errorMessage) {
        Token t = peek();
        if (t.type != type) {
            throw new ParseException(errorMessage + ", but found '" + t.value + "'", t.line, t.column);
        }
        pos++;
        return t;
    }

    /**
     * Checks if parser has reached end of input
     * @return true if no more tokens to process, false otherwise
     */
    private boolean isAtEnd() { return peek().type == Token.Type.EOF; }

    /**
     * Error recovery - skip tokens until reaching a likely statement start
     * This allows the parser to continue after errors and report multiple errors
     */
    private void synchronize() {
        while (!isAtEnd()) {
            // If previous token was semicolon, we're probably at statement end
            if (previous().type == Token.Type.SEMICOLON) return;
            
            // Skip until we find a token that can start a statement
            switch (peek().type) {
                case IF:           // if statement
                case IDENTIFIER:   // assignment statement
                    return; // Found start of next statement
            }
            pos++; // Skip this token and continue searching
        }
    }
}
