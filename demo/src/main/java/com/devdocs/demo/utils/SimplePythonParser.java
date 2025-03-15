package com.devdocs.demo.utils;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimplePythonParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode parsePythonCode(String code) throws IOException {
        CharStream input = CharStreams.fromString(code);
        Python3Lexer lexer = new Python3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        parser.setBuildParseTree(true);
        Python3Parser.File_inputContext tree = parser.file_input();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new IllegalArgumentException("Syntax errors detected in the Python code.");
        }

        PythonStructureListener listener = new PythonStructureListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        ObjectNode result = mapper.createObjectNode();
        result.set("imports", mapper.valueToTree(listener.getImports()));
        result.set("functions", mapper.valueToTree(listener.getFunctions()));
        result.set("classes", mapper.valueToTree(listener.getClasses()));

        return result;
    }

    public static class PythonStructureListener extends Python3ParserBaseListener {
        private final List<String> imports = new ArrayList<>();
        private final List<String> functions = new ArrayList<>();
        private final List<String> classes = new ArrayList<>();

        @Override
        public void enterImport_name(Python3Parser.Import_nameContext ctx) {
            if (ctx.dotted_as_names() != null) {
                for (var dottedAsName : ctx.dotted_as_names().dotted_as_name()) {
                    StringBuilder importName = new StringBuilder();
                    Python3Parser.Dotted_nameContext dottedName = dottedAsName.dotted_name();
                    if (dottedName != null) {
                        List<Python3Parser.NameContext> names = Collections.singletonList(dottedName.name());
                        if (names != null && !names.isEmpty()) {
                            for (var name : names) {
                                if (importName.length() > 0) importName.append(".");
                                importName.append(name.getText());
                            }
                        } else {
                            System.out.println("Warning: No name contexts found in dotted_name: " + dottedName.getText());
                        }
                    } else {
                        System.out.println("Warning: dotted_name is null in dotted_as_name context");
                    }

                    Python3Parser.NameContext aliasName = dottedAsName.name();
                    if (dottedAsName.AS() != null && aliasName != null) {
                        importName.append(" as ").append(aliasName.getText());
                    }
                    imports.add(importName.toString());
                }
            }
        }

        @Override
        public void enterFunction_def(Python3Parser.Function_defContext ctx) {
            Python3Parser.Function_def_rawContext rawContext = ctx.function_def_raw();
            if (rawContext != null) {
                Python3Parser.NameContext nameCtx = rawContext.name();
                if (nameCtx != null) {
                    functions.add(nameCtx.getText());
                } else {
                    System.out.println("Warning: name is null in function_def_raw context: " + rawContext.getText());
                }
            } else {
                System.out.println("Warning: function_def_raw is null in function_def context: " + ctx.getText());
            }
        }

        @Override
        public void enterClass_def(Python3Parser.Class_defContext ctx) {
            Python3Parser.Class_def_rawContext rawContext = ctx.class_def_raw();
            if (rawContext != null) {
                Python3Parser.NameContext nameCtx = rawContext.name();
                if (nameCtx != null) {
                    classes.add(nameCtx.getText());
                } else {
                    System.out.println("Warning: name is null in class_def_raw context: " + rawContext.getText());
                }
            } else {
                System.out.println("Warning: class_def_raw is null in class_def context: " + ctx.getText());
            }
        }

        public List<String> getImports() { return imports; }
        public List<String> getFunctions() { return functions; }
        public List<String> getClasses() { return classes; }
    }

    public static void main(String[] args) {
        try {
            String pythonCode = """
                    import math
                    import os as o
                    def hello():
                        pass
                    class MyClass:
                        pass
                    """;
            JsonNode result = parsePythonCode(pythonCode);
            System.out.println("Parsed JSON: " + result.toPrettyString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}