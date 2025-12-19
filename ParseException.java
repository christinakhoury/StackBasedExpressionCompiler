/**
 * Custom exception class for syntax errors during parsing.
 * Thrown when the parser encounters invalid syntax according to the language grammar.
 * Examples: missing parentheses, unexpected tokens, invalid expression structure.
*/
public class ParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	private final int line; // Source code line where error occurred
    private final int column; // Source code column where error occurred

    /**
     * Constructor for parse exceptions with location information
     * @param msg Descriptive error message
     * @param line Line number where error occurred
     * @param column Column number where error occurred
    */
    public ParseException(String msg, int line, int column) {
        super(msg + " at line " + line + ", column " + column); // Build detailed message
        this.line = line;
        this.column = column;
    }
    
    // Getters for error location
    public int getLine() { return line; }
    public int getColumn() { return column; }
}