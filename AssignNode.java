/**
 * Represents a variable assignment statement in the AST.
 * Example: "x = 5 + 3" or "result = (a + b) * c"
*/
public class AssignNode extends ASTNode {
    public String variable;    // The variable name being assigned to (left of '=')
    public ASTNode expression; // The expression being assigned (right of '=')

    /**
     * Constructor for assignment nodes
     * @param variable The name of the variable to assign to
     * @param expression The expression whose value will be assigned to the variable
    */
    public AssignNode(String variable, ASTNode expression) {
        this.variable = variable;//stores the variable name in the object.
        this.expression = expression;//stores the expression AST node in the object.
    }
}

/* AssignNode is a class for representing assignment statements in an AST.
variable → the name on the left side of =
expression → the AST representation of the right side
It’s a fundamental building block for interpreting or compiling code.*/