/**
 * Represents an if-else conditional statement in the AST.
 * Examples: 
 *   "if (x > 5) y = 1;"
 *   "if (condition) a = b; else a = c;"
*/
public class IfNode extends ASTNode {
	public ASTNode condition;   // The condition expression to evaluate
    public ASTNode thenBranch;  // The statement to execute if condition is true
    public ASTNode elseBranch;  // The statement to execute if condition is false (optional)

    /**
     * Constructor for if statement nodes
     * @param condition The boolean expression that determines which branch to take
     * @param thenBranch The statement to execute when condition is true
     * @param elseBranch The statement to execute when condition is false (can be null)
    */
    public IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
