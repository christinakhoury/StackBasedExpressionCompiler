import java.util.*;

/**
 * Represents a complete program as a sequence of statements in the AST.
 * This is the root node for programs containing multiple statements.
 * Example: a program with "x = 5; y = x + 1; if (y > 0) z = 1;"
*/
public class ProgramNode extends ASTNode {
    public List<ASTNode> statements; // List of all statements in the program

    /**
     * Constructor for program nodes
     * @param statements List of statement nodes that make up the program
    */
    public ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }
}