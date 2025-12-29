# Stack-Based Expression Compiler

## Description
This project is a **stack-based expression compiler** developed for the CSC352 course at Holy Spirit University of Kaslik (Fall 2025). It translates arithmetic expressions written in **standard infix notation** (e.g., `a = 3 + 4 * (2 - 1)`) into **stack-based intermediate instructions** such as `PUSH`, `ADD`, `SUB`, `MUL`, and `DIV`. The compiler includes three main components: **lexer**, **parser**, and **code generator**, demonstrating key concepts in **lexical analysis, parsing, AST construction, and syntax-directed translation**.

---

## Features
This compiler performs lexical analysis by tokenizing identifiers, numbers, operators, parentheses, and assignment symbols. It uses a recursive descent parser that respects operator precedence and parentheses, builds an abstract syntax tree (AST) for expressions and statements, and translates expressions into stack-based instructions. Sample test programs are included to demonstrate functionality. Optional bonus features include support for boolean expressions, if/else conditionals, AST visualization using Graphviz, and syntax error handling.

---

## Technologies Used
This project is implemented in **Java** using **Eclipse IDE**, with optional **Graphviz** for AST visualization.

---

## Project Structure
The repository is organized as follows: `StackBasedExpressionCompiler/` contains the `src/` folder for the source code (lexer, parser, AST, and code generator), the `tests/` folder for sample input programs and generated stack outputs, the `docs/` folder for AST visualizations (optional), and `README.md` for project documentation.

---

## How to Run
To run the compiler, first clone the repository using `git clone https://github.com/christinakhoury/StackBasedExpressionCompiler.git`. Open the project in Eclipse or any Java IDE, compile the Java files located in `/src/`, and run the main class. If the main class is `Interpreter.java`, run that, or run `ParserAST.java` depending on which executes the compiler logic. Check the `/tests/` folder for sample input programs and their generated stack instructions.

---

## What I Learned
Through this project, I learned how to build a **lexer and parser from scratch**, construct and traverse **abstract syntax trees (ASTs)**, translate high-level arithmetic expressions into **stack-based instructions**, organize a multi-file project with clear structure, and apply concepts from **context-free grammars and syntax-directed translation**.

---

## Optional Features Implemented
Optional features implemented include boolean expressions, conditional statements (`if`, `else`), AST visualization using Graphviz, and syntax error handling.

---

## Contact
- GitHub: [github.com/christinakhoury](https://github.com/christinakhoury)
