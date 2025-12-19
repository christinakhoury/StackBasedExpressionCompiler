/**
 * Represents a numeric literal value in the AST.
 * Examples: "42", "3.14", "-5.0"
 * Stores the numeric value as a double to handle both integers and decimals.
*/
public class NumberNode extends ASTNode {
    public double value; // The actual numeric value
    
    /**
     * Constructor for number literal nodes
     * @param value The numeric value
    */
    public NumberNode(double value) {
        this.value = value;
    }
}
