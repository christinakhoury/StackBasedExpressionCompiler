/**
 * Represents a variable identifier (name) in the AST.
 * Examples: "x", "total", "userName"
 * This node represents a reference to a variable, not its declaration.
*/
public class IdentifierNode extends ASTNode {
    public String name; // The name of the variable being referenced
    
    /**
     * Constructor for identifier nodes
     * @param name The name of the variable
    */
    public IdentifierNode(String name) {
        this.name = name;
    }
}
