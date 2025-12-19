import java.util.*;
import java.io.*;

/**
 * ASTVisualizer - Generates visual representations of Abstract Syntax Trees using Graphviz DOT format.
 * This class creates graphical visualizations of the AST structure for debugging and educational purposes.
 */
public class ASTVisualizer {
    private final StringBuilder dotBuilder = new StringBuilder(); // Builds the DOT language content
    private int nodeId = 0;                                      // Counter for generating unique node IDs
    private final Map<ASTNode, String> nodeIds = new HashMap<>(); // Maps AST nodes to their DOT node IDs

    /**
     * Generates a DOT language representation of the AST for Graphviz
     * @param node The root node of the AST to visualize
     * @return String containing the complete DOT graph specification
     */
    public String generateDOT(ASTNode node) {
        dotBuilder.setLength(0); // Clear any previous content
        nodeId = 0;              // Reset node counter
        nodeIds.clear();         // Clear node ID mapping
        
        // DOT graph header with styling properties
        dotBuilder.append("digraph AST {\n");
        dotBuilder.append("  node [shape=box, fontname=\"Arial\", fontsize=10];\n");
        dotBuilder.append("  edge [fontname=\"Arial\", fontsize=8];\n\n");
        
        // Recursively build the graph structure starting from root
        buildGraph(node, null);
        
        dotBuilder.append("}\n");
        return dotBuilder.toString();
    }
    
    /**
     * Saves the DOT representation to a file
     * @param filename The name of the DOT file to create
     * @param node The AST root node to visualize
     * @throws IOException if file writing fails
     */
    public void saveDOTToFile(String filename, ASTNode node) throws IOException {
        String dot = generateDOT(node);
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.print(dot);
        }
    }
    
    /**
     * Generates both DOT file and PNG image using Graphviz (if available)
     * @param dotFilename Name for the intermediate DOT file
     * @param pngFilename Name for the output PNG image
     * @param node The AST root node to visualize
     * @throws IOException if file operations fail
     */
    public void generateAndSavePNG(String dotFilename, String pngFilename, ASTNode node) throws IOException {
        saveDOTToFile(dotFilename, node);
        // Make a folder called "ASTs" on your Desktop first

        pngFilename = "C:\\Users\\Lenovo\\Desktop\\ASTs\\" + pngFilename;



        try {
            // Execute Graphviz 'dot' command to convert DOT to PNG
            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\Users\\Lenovo\\Desktop\\Graphviz-14.0.5-win64\\bin\\dot.exe",
                    "-Tpng",
                    dotFilename,
                    "-o",
                    pngFilename
            );

            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("AST visualization saved as: " + pngFilename);
            } else {
                System.out.println("Warning: Could not generate PNG. Make sure Graphviz is installed.");
                System.out.println("DOT file saved as: " + dotFilename);
            }
        } catch (Exception e) {
            System.out.println("Warning: Graphviz not available. DOT file saved as: " + dotFilename);
        }
    }

    /**
     * Recursively builds the DOT graph representation of the AST
     * @param node Current AST node being processed
     * @param parentId DOT ID of the parent node (null for root node)
     * @return DOT ID of the current node for reference by parent
     */
    private String buildGraph(ASTNode node, String parentId) {
        if (node == null) return null;
        
        // Generate unique ID for this node and store mapping
        String currentNodeId = "node" + (nodeId++);
        nodeIds.put(node, currentNodeId);
        
        // Define the current node based on its type
        if (node instanceof NumberNode) {
            NumberNode num = (NumberNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"Number\\n").append(num.value).append("\"];\n");
        } 
        else if (node instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"Identifier\\n").append(id.name).append("\"];\n");
        }
        else if (node instanceof BooleanNode) {
            BooleanNode bool = (BooleanNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"Boolean\\n").append(bool.value).append("\"];\n");
        }
        else if (node instanceof UnaryOpNode) {
            UnaryOpNode unary = (UnaryOpNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"UnaryOp\\n").append(unary.operator).append("\"];\n");
            
            // Recursively build child node (the expression) and connect with edge
            String childId = buildGraph(unary.expr, currentNodeId);
            if (childId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(childId)
                          .append(" [label=\"expr\"];\n");
            }
        }
        else if (node instanceof BinaryOpNode) {
            BinaryOpNode bin = (BinaryOpNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"BinaryOp\\n").append(bin.operator).append("\"];\n");
            
            // Recursively build both left and right child nodes
            String leftId = buildGraph(bin.left, currentNodeId);
            String rightId = buildGraph(bin.right, currentNodeId);
            
            if (leftId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(leftId)
                          .append(" [label=\"left\"];\n");
            }
            if (rightId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(rightId)
                          .append(" [label=\"right\"];\n");
            }
        }
        else if (node instanceof AssignNode) {
            AssignNode assign = (AssignNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"Assign\\n").append(assign.variable).append("\"];\n");
            
            // Build the expression being assigned
            String exprId = buildGraph(assign.expression, currentNodeId);
            if (exprId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(exprId)
                          .append(" [label=\"expr\"];\n");
            }
        }
        else if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"If\", shape=diamond];\n"); // Diamond shape for conditionals
            
            // Build condition, then branch, and else branch (if present)
            String condId = buildGraph(ifNode.condition, currentNodeId);
            String thenId = buildGraph(ifNode.thenBranch, currentNodeId);
            String elseId = buildGraph(ifNode.elseBranch, currentNodeId);
            
            if (condId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(condId)
                          .append(" [label=\"condition\"];\n");
            }
            if (thenId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(thenId)
                          .append(" [label=\"then\"];\n");
            }
            if (elseId != null) {
                dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(elseId)
                          .append(" [label=\"else\"];\n");
            }
        }
        else if (node instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) node;
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"Program\", shape=ellipse, color=blue];\n"); // Ellipse for program root
            
            // Build all statements in the program with indexed labels
            for (int i = 0; i < program.statements.size(); i++) {
                String stmtId = buildGraph(program.statements.get(i), currentNodeId);
                if (stmtId != null) {
                    dotBuilder.append("  ").append(currentNodeId).append(" -> ").append(stmtId)
                              .append(" [label=\"stmt ").append(i).append("\"];\n");
                }
            }
        }
        else {
            // Unknown node type - display with error styling
            dotBuilder.append("  ").append(currentNodeId)
                      .append(" [label=\"Unknown\\n").append(node.getClass().getSimpleName()).append("\", color=red];\n");
        }
        
        // Connect this node to its parent in the tree (if not root)
        if (parentId != null) {
            dotBuilder.append("  ").append(parentId).append(" -> ").append(currentNodeId).append(";\n");
        }
        
        return currentNodeId;
    }
}
