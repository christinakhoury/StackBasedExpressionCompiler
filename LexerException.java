/**
 * Custom exception class for lexical analysis errors.
 * Thrown when the tokenizer encounters invalid characters or patterns in the source code.
 * Examples: unknown symbols, malformed numbers, unclosed comments.
*/
public class LexerException extends RuntimeException {
	private static final long serialVersionUID = 1L; // eclipse added it

	/**
     * Constructor for lexical exceptions
     * @param msg Descriptive error message explaining the lexical error
    */
	public LexerException(String msg) { 
		super(msg); 
	}
}

