/**
 * Represents a unary operation in the AST.
 * Unary operations have one operand and one operator.
 * Examples: "-5", "!condition", "-(x + y)"
*/
public class UnaryOpNode extends ASTNode {
    public String operator; // The unary operator (- for negation, ! for logical NOT)
    public ASTNode expr;    // The operand expression

    /**
     * Constructor for unary operation nodes
     * @param operator The unary operator symbol
     * @param expr The operand expression
    */
    public UnaryOpNode(String operator, ASTNode expr) {
        this.operator = operator;
        this.expr = expr;
    }
}
/*This class specifically handles unary operations, which have these characteristics:
One operator: such as - (negation) or ! (logical NOT).
One operand: an expression that the operator acts on.*/