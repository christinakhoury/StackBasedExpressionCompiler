/**
 * Represents a binary operation in the AST.
 * Binary operations have two operands and one operator.
 * Examples: "a + b", "x * y", "5 > 3", "condition1 && condition2"
*/
public class BinaryOpNode extends ASTNode {
    public String operator;      // The binary operator (+, -, *, /, ==, && ...)
    public ASTNode left, right;  // The left operand expression and The right operand expression

    /**
     * Constructor for binary operation nodes
     * @param operator The binary operator symbol
     * @param left The left-hand side expression
     * @param right The right-hand side expression
    */
    public BinaryOpNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
}
