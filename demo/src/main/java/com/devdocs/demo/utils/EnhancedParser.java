//package com.devdocs.demo.utils;
//
//import org.antlr.v4.runtime.*;
//import org.antlr.v4.runtime.tree.ParseTreeWalker;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//
//import java.io.IOException;
//import java.util.*;
//
//public class EnhancedPythonParser {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    public static JsonNode parsePythonCode(String code) throws IOException {
//        CharStream input = CharStreams.fromString(code);
//        Python3Lexer lexer = new Python3Lexer(input);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        Python3Parser parser = new Python3Parser(tokens);
//        parser.setBuildParseTree(true);
//        Python3Parser.File_inputContext tree = parser.file_input();
//
//        if (parser.getNumberOfSyntaxErrors() > 0) {
//            throw new IllegalArgumentException("Syntax errors detected in the Python code.");
//        }
//
//        PythonStructureListener listener = new PythonStructureListener();
//        ParseTreeWalker walker = new ParseTreeWalker();
//        walker.walk(listener, tree);
//
//        ObjectNode result = mapper.createObjectNode();
//        result.set("imports", mapper.valueToTree(listener.getImports()));
//        result.set("classes", mapper.valueToTree(listener.getClasses()));
//        result.set("functions", mapper.valueToTree(listener.getFunctions()));
//        result.set("function_dependencies", mapper.valueToTree(listener.getFunctionDependencies()));
//
//        return result;
//    }
//
//    public static class PythonStructureListener extends Python3ParserBaseListener {
//        private final List<String> imports = new ArrayList<>();
//        private final List<ObjectNode> functions = new ArrayList<>();
//        private final List<ObjectNode> classes = new ArrayList<>();
//        private final Map<String, Set<String>> functionCalls = new HashMap<>();
//        private String currentFunction = null;
//
//        @Override
//        public void enterImport_name(Python3Parser.Import_nameContext ctx) {
//            imports.add(ctx.getText());
//        }
//
//        @Override
//        public void enterFunction_def(Python3Parser.Function_defContext ctx) {
//            Python3Parser.Function_def_rawContext rawContext = ctx.function_def_raw();
//            if (rawContext != null) {
//                Python3Parser.NameContext nameCtx = rawContext.name();
//                if (nameCtx != null) {
//                    currentFunction = nameCtx.getText();
//                    functionCalls.put(currentFunction, new HashSet<>());
//
//                    ObjectNode functionNode = mapper.createObjectNode();
//                    functionNode.put("name", currentFunction);
//                    functionNode.put("returnType", "Unknown");  // ANTLR doesn't explicitly provide return types
//
//                    // Extract parameters
//                    ArrayNode parameters = mapper.createArrayNode();
//                    if (rawContext.typedargslist() != null) {
//                        for (Python3Parser.TfpdefContext param : rawContext.typedargslist().tfpdef()) {
//                            parameters.add(param.getText());
//                        }
//                    }
//                    functionNode.set("parameters", parameters);
//                    functionNode.put("body", ctx.getText());
//                    functionNode.set("annotations", mapper.createArrayNode());
//
//                    functions.add(functionNode);
//                }
//            }
//        }
//
//        @Override
//        public void enterClass_def(Python3Parser.Class_defContext ctx) {
//            Python3Parser.Class_def_rawContext rawContext = ctx.class_def_raw();
//            if (rawContext != null) {
//                Python3Parser.NameContext nameCtx = rawContext.name();
//                if (nameCtx != null) {
//                    ObjectNode classNode = mapper.createObjectNode();
//                    classNode.put("name", nameCtx.getText());
//
//                    // Extract extended classes
//                    ArrayNode extendedClasses = mapper.createArrayNode();
//                    if (rawContext.arglist() != null) {
//                        for (Python3Parser.ArgumentContext baseClass : rawContext.arglist().argument()) {
//                            extendedClasses.add(baseClass.getText());
//                        }
//                    }
//                    classNode.set("extendedClasses", extendedClasses);
//                    classNode.set("annotations", mapper.createArrayNode());
//
//                    // Extract methods inside the class
//                    ArrayNode methods = mapper.createArrayNode();
//                    for (Python3Parser.FuncdefContext methodCtx : ctx.funcdef()) {
//                        ObjectNode methodNode = mapper.createObjectNode();
//                        methodNode.put("name", methodCtx.NAME().getText());
//                        methodNode.put("returnType", "Unknown");
//                        methodNode.put("body", methodCtx.getText());
//                        methodNode.set("annotations", mapper.createArrayNode());
//                        methods.add(methodNode);
//                    }
//                    classNode.set("methods", methods);
//                    classes.add(classNode);
//                }
//            }
//        }
//
//        @Override
//        public void enterAtom_expr(Python3Parser.Atom_exprContext ctx) {
//            if (currentFunction != null && ctx.getText().matches("\\w+\\(.*\\)")) {
//                String calledFunction = ctx.getText().split("\\(")[0];
//                functionCalls.get(currentFunction).add(calledFunction);
//            }
//        }
//
//        public List<String> getImports() { return imports; }
//        public List<ObjectNode> getFunctions() { return functions; }
//        public List<ObjectNode> getClasses() { return classes; }
//        public Map<String, Set<String>> getFunctionDependencies() { return functionCalls; }
//    }
//
//    public static void main(String[] args) {
//        try {
//            String pythonCode = """
//                    import math
//                    import os as o
//
//                    def hello():
//                        print("Hello, world!")
//
//                    def add(a, b):
//                        return a + b
//
//                    class MyClass:
//                        def method1(self):
//                            return "method1"
//
//                        def method2(self):
//                            return self.method1()
//                    """;
//
//            JsonNode result = parsePythonCode(pythonCode);
//            System.out.println("Parsed JSON: " + result.toPrettyString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
