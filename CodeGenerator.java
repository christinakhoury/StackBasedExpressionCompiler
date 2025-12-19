import java.util.*;

/**
 * Code Generator - Converts Abstract Syntax Tree (AST) to stack-based instructions
 * This is the final phase of compilation that produces executable code
*/
public class CodeGenerator {
    private final List<String> instructions = new ArrayList<>(); // Output instruction list

    /**
     * Main generation method - converts entire AST to instructions
     * @param node The root node of the AST to generate code for
     * @return List of stack machine instructions
    */
    public List<String> generate(ASTNode node) {
        visit(node);  // Start recursive code generation
        return instructions;
    }

    /**
     * Recursive visitor pattern - dispatches to appropriate generation method
     * based on the actual type of AST node
     * @param node The AST node to generate code for
    */
    private void visit(ASTNode node) {
    	// Handle different types of AST nodes
        if (node instanceof NumberNode) {
        	// Generate code for numeric literals: PUSH <value>
            instructions.add("PUSH " + ((NumberNode) node).value);
            
        } else if (node instanceof IdentifierNode) {
        	// Generate code for variable references: PUSH <variableName>
            instructions.add("PUSH " + ((IdentifierNode) node).name);
            
        } else if (node instanceof BooleanNode) {
        	// Generate code for boolean literals: PUSH 1 (true) or PUSH 0 (false)
            instructions.add("PUSH " + (((BooleanNode) node).value ? 1 : 0));
            
        } else if (node instanceof UnaryOpNode) {
        	// Generate code for unary operations: -x or !x
            UnaryOpNode u = (UnaryOpNode) node;
            visit(u.expr); // First generate code for the operand
            
            // Then apply the unary operator
            switch (u.operator) {
                case "-" -> instructions.add("NEG"); // Negation: PUSH 5 → NEG → PUSH -5
                case "!" -> instructions.add("NOT"); // Logical NOT: PUSH 1 → NOT → PUSH 0
            }
            
        } else if (node instanceof BinaryOpNode) {
        	// Generate code for binary operations: left OP right
            BinaryOpNode b = (BinaryOpNode) node;
            
            // Generate code for left operand (goes on stack first)
            visit(b.left);
            // Generate code for right operand (goes on stack second)
            visit(b.right);
            
            // Apply the binary operator (pops two values, pushes result)
            switch (b.operator) {
                // Arithmetic operators
                case "+" -> instructions.add("ADD"); // a + b
                case "-" -> instructions.add("SUB"); // a - b
                case "*" -> instructions.add("MUL"); // a * b
                case "/" -> instructions.add("DIV"); // a / b
                case "^", "**" -> instructions.add("POW"); // a ^ b (exponentiation)
                case "%" -> instructions.add("MOD");  // a % b (modulus)
                
                // Comparison operators (return 1 for true, 0 for false)
                case "==" -> instructions.add("EQ");   // a == b
                case "!=" -> instructions.add("NEQ");  // a != b
                case "<" -> instructions.add("LT");    // a < b
                case "<=" -> instructions.add("LTE");  // a <= b
                case ">" -> instructions.add("GT");    // a > b
                case ">=" -> instructions.add("GTE");  // a >= b
                
                // Logical operators
                case "&&" -> instructions.add("AND");  // a && b (logical AND)
                case "||" -> instructions.add("OR");   // a || b (logical OR)
                
            }
            
        } else if (node instanceof AssignNode) {
        	// Generate code for variable assignment: STORE <variableName>
            AssignNode a = (AssignNode) node;
            visit(a.expression); // Generate code for the value to assign
            instructions.add("STORE " + a.variable); // Store result in variable
            
        } else if (node instanceof IfNode) {
        	// Generate code for if-else statements (bonus)
            IfNode ifNode = (IfNode) node;
            
            // Generate condition code - result will be on stack (1=true, 0=false)
            visit(ifNode.condition);
            
            // Create unique labels for jump targets using object hash codes
            // Jump if false
            String elseLabel = "ELSE_" + System.identityHashCode(ifNode);
            String endLabel = "END_" + System.identityHashCode(ifNode);
            
            // Jump to else branch if condition is false (0)
            instructions.add("JUMP_IF_FALSE " + elseLabel);
            
            // Generate code for then branch (executed if condition true)
            visit(ifNode.thenBranch);
            
            // Jump over else branch after executing then branch
            instructions.add("JUMP " + endLabel);
            
            // Else branch label and code
            instructions.add("LABEL " + elseLabel);
            if (ifNode.elseBranch != null) {
                visit(ifNode.elseBranch);  // Generate code for else branch
            }
            
            // End of if statement label
            instructions.add("LABEL " + endLabel);
        }
    }
}