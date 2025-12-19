import java.util.*;

public class Interpreter {
    
    public static void runTest(String testName, String input) {
        System.out.println("=== " + testName + " ===");
        System.out.println("Input: " + input.replace("\n", "\\n"));
        
        try {
            // 1️⃣ Tokenize
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.tokenize();

            System.out.println("\nTokens:");
            for (Token t : tokens) {
                if (t.type != Token.Type.EOF) {
                    System.out.println(t);
                }
            }

            // 2️⃣ Parse AST
            ParserAST parserAST = new ParserAST(tokens);
            List<ASTNode> statements = parserAST.parseStatements();
            
            // 3️⃣ Generate AST Visualization (NEW CODE)
            if (!statements.isEmpty()) {
                ASTVisualizer visualizer = new ASTVisualizer();
                String dotFilename = testName.replaceAll("[^a-zA-Z0-9]", "_") + ".dot";
                String pngFilename = testName.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
                
                // For multiple statements, visualize the first one or create a program node
                if (statements.size() == 1) {
                    visualizer.generateAndSavePNG(dotFilename, pngFilename, statements.get(0));
                } else {
                    // Create a simple program node for multiple statements
                    ProgramNode program = new ProgramNode(statements);
                    visualizer.generateAndSavePNG(dotFilename, pngFilename, program);
                }
            }
            
            // 4️⃣️ Generate Instructions
            CodeGenerator codeGen = new CodeGenerator();
            List<String> instructions = new ArrayList<>();
            for (ASTNode stmt : statements) {
                instructions.addAll(codeGen.generate(stmt));
            }

            System.out.println("\nGenerated Instructions:");
            for (String instr : instructions) System.out.println(instr);

            // 4️⃣ Execute Instructions
            Map<String, Double> variables = executeInstructions(instructions);

            System.out.println("\nFinal Variables:");
            variables.forEach((k,v) -> System.out.println(k + " = " + v));
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        System.out.println("=".repeat(50) + "\n");
    }
    
    private static Map<String, Double> executeInstructions(List<String> instructions) {
        Map<String, Double> variables = new HashMap<>();
        Stack<Double> stack = new Stack<>();
        Map<String, Integer> labels = new HashMap<>();
        
        // First pass: find labels
        for (int i = 0; i < instructions.size(); i++) {
            String instr = instructions.get(i);
            if (instr.startsWith("LABEL ")) {
                labels.put(instr.substring(6), i);
            }
        }
        
        // Second pass: execute
        int pc = 0; // program counter
        while (pc < instructions.size()) {
            String instr = instructions.get(pc);
            String[] parts = instr.split(" ");
            
            switch (parts[0]) {
                case "PUSH" -> {
                    try {
                        stack.push(Double.parseDouble(parts[1]));
                    } catch (NumberFormatException e) {
                        if (!variables.containsKey(parts[1])) {
                            throw new RuntimeException("Variable " + parts[1] + " not defined.");
                        }
                        stack.push(variables.get(parts[1]));
                    }
                }
                case "ADD" -> stack.push(stack.pop() + stack.pop());
                case "SUB" -> { double b = stack.pop(), a = stack.pop(); stack.push(a - b); }
                case "MUL" -> stack.push(stack.pop() * stack.pop());
                case "DIV" -> { 
                    double b = stack.pop(), a = stack.pop(); 
                    if (b == 0) throw new RuntimeException("Division by zero");
                    stack.push(a / b); 
                }
                case "POW" -> { double exp = stack.pop(), base = stack.pop(); stack.push(Math.pow(base, exp)); }
                case "MOD" -> { double b = stack.pop(), a = stack.pop(); stack.push(a % b); }
                case "NEG" -> stack.push(-stack.pop());
                case "NOT" -> stack.push(stack.pop() == 0 ? 1.0 : 0.0);
                // Boolean and comparison operations
                case "EQ" -> { double b = stack.pop(), a = stack.pop(); stack.push(a == b ? 1.0 : 0.0); }
                case "NEQ" -> { double b = stack.pop(), a = stack.pop(); stack.push(a != b ? 1.0 : 0.0); }
                case "LT" -> { double b = stack.pop(), a = stack.pop(); stack.push(a < b ? 1.0 : 0.0); }
                case "LTE" -> { double b = stack.pop(), a = stack.pop(); stack.push(a <= b ? 1.0 : 0.0); }
                case "GT" -> { double b = stack.pop(), a = stack.pop(); stack.push(a > b ? 1.0 : 0.0); }
                case "GTE" -> { double b = stack.pop(), a = stack.pop(); stack.push(a >= b ? 1.0 : 0.0); }
                case "AND" -> { double b = stack.pop(), a = stack.pop(); stack.push((a != 0 && b != 0) ? 1.0 : 0.0); }
                case "OR" -> { double b = stack.pop(), a = stack.pop(); stack.push((a != 0 || b != 0) ? 1.0 : 0.0); }
                case "STORE" -> variables.put(parts[1], stack.pop());
                // Control flow instructions
                case "JUMP_IF_FALSE" -> {
                    if (stack.pop() == 0) {
                        pc = labels.get(parts[1]);
                    }
                }
                case "JUMP" -> pc = labels.get(parts[1]);
                case "LABEL" -> { /* Do nothing - labels are handled in first pass */ }
                default -> throw new RuntimeException("Unknown instruction: " + parts[0]);
            }
            pc++;
        }
        return variables;
    }
    
    public static void main(String[] args) {
        // Run comprehensive test suite
        System.out.println("COMPREHENSIVE TEST SUITE\n");
        
        // Original tests (should work)
        runTest("Basic Arithmetic", "a = 3 + 4 * (2 - 1); b = a + 5 / 2;");
        runTest("Advanced Operations", "x = 10; y = 2 ^ 3 + x % 3; z = -x * (y + 1);");
        runTest("Complex Expression", "result = (5 + 3) * 2 - 8 / 4; temp = result * 2 ** 3;");
        runTest("Unary Operations", "a = -5; b = -(-3); c = a + b;");
        
        // New tests for enhanced features
        runTest("Boolean Expressions", "x = 5 > 3; y = (2 + 2 == 4) && (3 < 2); z = !(1 == 0);");
        runTest("If Statement", "a = 10; if (a > 5) b = 1; else b = 0;");
        runTest("Simple Comparison", "x = 5 > 3; y = 2 < 1;");
        
        // Test error cases
        runTest("Error Test - Undefined Variable", "x = y + 1;");
        runTest("Error Test - Division by Zero", "x = 5 / 0;");
    }
}
