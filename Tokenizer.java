import java.util.*;

public class Tokenizer {
    private final String input;  // The complete source code to tokenize
    private int pos = 0;		// Current position in the input string
    private int line = 1;		// Current line number (starts at 1)
    private int column = 1;		// Current column number (starts at 1)

    /**
     * Constructor - initializes with source code
     * @param input The program source code as a string
    */
    public Tokenizer(String input) {
        this.input = input;
    }

    /**
     * Main tokenization method - converts entire input to tokens
     * @return List of tokens representing the source code
     * @throws LexerException if invalid characters or syntax are encountered
    */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        /*tokenize() is the main method: converts code into a list of tokens.
        Creates an empty list to store tokens as we scan.*/
        
        // Process each character in the input
        while (pos < input.length()) {
            char c = input.charAt(pos);
            
            // Skip whitespace (spaces, tabs, newlines)
            if (Character.isWhitespace(c)) {
                if (c == '\n') {
                	// Newline: increment line counter, reset column
                    line++;
                    column = 1;
                } else {
                	// Other whitespace: just move column forward
                    column++;
                }
                pos++;
                continue; // Move to next character
            }

            // Handle comments (the bonus part)
            if (c == '/' && pos + 1 < input.length() && input.charAt(pos + 1) == '/') {
                skipLineComment(); // Skip everything until end of line
                continue;
            }
            
            if (c == '/' && pos + 1 < input.length() && input.charAt(pos + 1) == '*') {
                skipBlockComment(); // Skip everything until closing
                continue;
            }

            // Process actual code tokens
            try {
                if (Character.isDigit(c)) {
                	// Numbers: 123, 45.67 ...
                    tokens.add(readNumber());
                } else if (Character.isLetter(c)) {
                	// Identifiers or keywords: x, if, else, true ...
                    tokens.add(readIdentifierOrKeyword());
                } else {
                	// Symbols: +, -, =, (, ),
                    tokens.add(readSymbol());
                }
            } catch (Exception e) {
                throw new LexerException("Lexical error at line " + line + ", column " + column + ": " + e.getMessage());
            }
        }
        // Add End-of-File token to mark completion
        tokens.add(new Token(Token.Type.EOF, "", pos, line, column));
        return tokens;
    }
    
    /**
     * Skips single-line comments (// comment until end of line)
    */
    private void skipLineComment() {
    	// Consume everything until newline or end of input
        while (pos < input.length() && input.charAt(pos) != '\n') {
            pos++;
            column++;
        }
        // Skip the newline character too (if present)
        if (pos < input.length() && input.charAt(pos) == '\n') {
            pos++;
            line++;
            column = 1; // Reset column at start of new line
        }
    }
    
    /**
     * Skips multi-line comments (/* comment * /)
    */
    private void skipBlockComment() {
        int startLine = line;          // Remember where comment started for error reporting
        int startColumn = column;
        pos += 2; // Skip the opening /* characters
        column += 2;
        
        // Search for closing */
        while (pos < input.length() - 1) {
            if (input.charAt(pos) == '*' && input.charAt(pos + 1) == '/') {
            	// Found closing */ - skip it and return
                pos += 2;
                column += 2;
                return;
            }
            // Handle newlines within block comments
            if (input.charAt(pos) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            pos++;
        }
        // If we get here, the comment was never closed
        throw new LexerException("Unclosed block comment starting at line " + startLine + ", column " + startColumn);
    }
    
    
    /**
     * Reads a numeric literal from input
     * Supports integers and decimals (but not ending with decimal point)
     * @return NUMBER token with the numeric value
    */
    private Token readNumber() {
        int start = pos;			// Remember starting position
        int startLine = line;		// Remember starting line
        int startColumn = column;	// Remember starting column
        boolean hasDecimal = false;	// Track if we've seen a decimal point
        
        // Read consecutive digits and at most one decimal point
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '.') {
                if (hasDecimal) {
                	// Error: multiple decimal points in same number
                    throw new LexerException("Multiple decimal points in number");
                }
                hasDecimal = true;
                pos++;
                column++;
            } else if (Character.isDigit(c)) {
            	// Regular digit - continue reading
                pos++;
                column++;
            } else {
            	// Non-numeric character - end of number
                break;
            }
        }
        
        // Extract the number string
        String numberStr = input.substring(start, pos);
        if (numberStr.endsWith(".")) {
        	// Error: number can't end with just a decimal point
            throw new LexerException("Number cannot end with decimal point");
        }
        // Create and return the NUMBER token
        return new Token(Token.Type.NUMBER, numberStr, start, startLine, startColumn);
    }
    
    /**
     * Reads an identifier or keyword from input
     * Identifiers: start with letter, can contain letters, digits, underscores
     * Keywords: if, else, true, false (have special token types)
     * @return Appropriate token (IDENTIFIER, IF, ELSE, or BOOLEAN)
    */
    private Token readIdentifierOrKeyword() {
        int start = pos;
        int startLine = line;
        int startColumn = column;
        
        // Read consecutive alphanumeric characters and underscores
        while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
            pos++;
            column++;
        }
        
        // Extract the identifier string
        String value = input.substring(start, pos);
        
        // Check if it's a reserved keyword and return appropriate token type
        switch (value) {
            case "if": return new Token(Token.Type.IF, value, start, startLine, startColumn);
            case "else": return new Token(Token.Type.ELSE, value, start, startLine, startColumn);
            case "true": return new Token(Token.Type.BOOLEAN, value, start, startLine, startColumn);
            case "false": return new Token(Token.Type.BOOLEAN, value, start, startLine, startColumn);
            default: return new Token(Token.Type.IDENTIFIER, value, start, startLine, startColumn);
        }
    }
    
    
    /**
     * Reads a symbol/operator from input
     * Handles single-character and multi-character symbols (==, !=, <=, etc.)
     * @return Appropriate token for the symbol
     * @throws LexerException for unknown characters
    */
    private Token readSymbol() {
        char c = input.charAt(pos);
        int start = pos;
        int startLine = line;
        int startColumn = column;
        
        // Handle each possible symbol type
        switch (c) {
            case '=':
                pos++;
                column++;
                // Check for '==' (equality operator) vs '=' (assignment)
                if (pos < input.length() && input.charAt(pos) == '=') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, "==", start, startLine, startColumn);
                }
                return new Token(Token.Type.ASSIGNMENT, "=", start, startLine, startColumn);
                
            case ';':
                pos++;
                column++;
                return new Token(Token.Type.SEMICOLON, ";", start, startLine, startColumn);
                
            case '(':
                pos++;
                column++;
                return new Token(Token.Type.LPAREN, "(", start, startLine, startColumn);
                
            case ')':
                pos++;
                column++;
                return new Token(Token.Type.RPAREN, ")", start, startLine, startColumn);
                
            case '{':
                pos++;
                column++;
                return new Token(Token.Type.LBRACE, "{", start, startLine, startColumn);
                
            case '}':
                pos++;
                column++;
                return new Token(Token.Type.RBRACE, "}", start, startLine, startColumn);
            
            // Single-character operators
            case '+': case '-': case '/': case '%': case '^':
                pos++;
                column++;
                return new Token(Token.Type.OPERATOR, String.valueOf(c), start, startLine, startColumn);
                
            case '*':
                pos++;
                column++;
                // Check for '**' (exponentiation) vs '*' (multiplication)
                if (pos < input.length() && input.charAt(pos) == '*') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, "**", start, startLine, startColumn);
                }
                return new Token(Token.Type.OPERATOR, "*", start, startLine, startColumn);
                
            case '<':
                pos++;
                column++;
                // Check for '<=' vs '<'
                if (pos < input.length() && input.charAt(pos) == '=') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, "<=", start, startLine, startColumn);
                }
                return new Token(Token.Type.OPERATOR, "<", start, startLine, startColumn);
                
            case '>':
                pos++;
                column++;
                // Check for '>=' vs '>'
                if (pos < input.length() && input.charAt(pos) == '=') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, ">=", start, startLine, startColumn);
                }
                return new Token(Token.Type.OPERATOR, ">", start, startLine, startColumn);
                
            case '!':
                pos++;
                column++;
                // Check for '!=' (inequality) vs '!' (logical NOT)
                if (pos < input.length() && input.charAt(pos) == '=') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, "!=", start, startLine, startColumn);
                }
                return new Token(Token.Type.OPERATOR, "!", start, startLine, startColumn);
                
            case '&':
                pos++;
                column++;
                // Logical AND requires '&&'
                if (pos < input.length() && input.charAt(pos) == '&') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, "&&", start, startLine, startColumn);
                }
                break; // Single '&' is invalid
                
            case '|':
                pos++;
                column++;
                // Logical OR requires '||'
                if (pos < input.length() && input.charAt(pos) == '|') {
                    pos++;
                    column++;
                    return new Token(Token.Type.OPERATOR, "||", start, startLine, startColumn);
                }
                break; // Single '|' is invalid
                
            default:
                throw new LexerException("Unknown character: '" + c + "'");
        }
        
        // If we get here, we encountered an invalid symbol
        throw new LexerException("Invalid symbol: '" + c + "'");
    }
}
/*Tokenizer breaks code into atomic pieces (tokens) that the parser or interpreter can work with, while keeping track of line/column info and handling comments, numbers, keywords, and symbols.*/