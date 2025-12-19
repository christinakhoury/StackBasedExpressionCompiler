/**
 * Represents a boolean literal value in the AST.
 * Examples: "true", "false"
 * Used in conditional expressions and boolean operations.
*/
public class BooleanNode extends ASTNode {
    public boolean value; // The actual boolean value (true or false)

    /**
     * Constructor for boolean literal nodes
     * @param value The boolean value (true or false)
    */
    public BooleanNode(boolean value) {
        this.value = value;
    }
}
