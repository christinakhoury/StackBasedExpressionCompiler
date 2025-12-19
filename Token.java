public class Token {
	/* Enumeration of all possible token types in the language */
    public enum Type {
        NUMBER,      // Numeric literals (e.g., 123, 45.67)
        IDENTIFIER,  // Variable names (e.g., x, myVar)
        ASSIGNMENT,  // Assignment operator '='
        OPERATOR,    // All other operators (+, -, *, /, ==, etc.)
        SEMICOLON,   // Statement terminator ';'
        LPAREN,      // Left parenthesis '('
        RPAREN,      // Right parenthesis ')'
        EOF,         // End of file marker
        
        // New types for conditionals and booleans (the bonus part)
        IF,          // 'if' keyword
        ELSE,        // 'else' keyword
        BOOLEAN,     // Boolean literals (true, false)
        LBRACE,      // Left brace '{'
        RBRACE       // Right brace '}'
    }

    public Type type;
    public String value; // The actual text content of the token
    public int pos;      // Position in the input string
    public int line;     // Line number in source code (for error reporting)
    public int column;   // Column number in source code (for error reporting)

    /**
     * Original constructor - uses default line/column values
     * @param type The token type
     * @param value The token's string value
     * @param pos Absolute position in input
    */
    public Token(Type type, String value, int pos) {
        this(type, value, pos, 1, 1); // Default to line 1, column 1
    }

    /**
     * Enhanced constructor with full location tracking
     * @param type The token type
     * @param value The token's string value  
     * @param pos Absolute position in input
     * @param line Line number in source
     * @param column Column number in source
    */
    public Token(Type type, String value, int pos, int line, int column) {
        this.type = type;
        this.value = value;
        this.pos = pos;
        this.line = line;
        this.column = column;
    }
    
    /**
     * Returns a human-readable string representation of the token
     * Includes location information for debugging
    */
    @Override
    public String toString() {
        if (line == 1 && column == 1) {
        	// Simple format for tokens without precise location
            return type + "(" + value + ") at " + pos;
        }
     // Detailed format with line and column information
        return type + "(" + value + ") at " + pos + " [line:" + line + ",col:" + column + "]";
    }
}
