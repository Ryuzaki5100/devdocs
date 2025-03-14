package com.devdocs.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SimplePythonParser {
    // List of possible Python executable paths
    private static final List<String> PYTHON_EXECUTABLES = Arrays.asList(
            System.getenv("PYTHON_EXECUTABLE"), // Environment variable (if set)
            "/usr/bin/python3",                 // Common on Ubuntu/Debian
            "/usr/local/bin/python3",           // Common on some Linux distros
            "/opt/python3/bin/python3",         // Possible in custom installs
            "python3",                          // Fallback to PATH
            "python"                            // Last resort
    );

    private static String PYTHON_EXECUTABLE;

    // Working directory defaults to the runtime directory
    private static final String WORKING_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();

    // Embedded content of pythonAnalyze.py
    private static final String PYTHON_ANALYZE_SCRIPT = """
            import ast
            import json
            import sys
            from collections import defaultdict

            class PythonFileStructure:
                def __init__(self):
                    self.imports = []
                    self.classes = []
                    self.functions = []

                class PythonClassStructure:
                    def __init__(self, name, extended_classes=None, annotations=None):
                        self.name = name
                        self.extended_classes = extended_classes or []
                        self.annotations = annotations or []
                        self.methods = []

                class PythonMethodStructure:
                    def __init__(self, name, return_type=None, parameters=None, body=None, annotations=None):
                        self.name = name
                        self.return_type = return_type or ""
                        self.parameters = parameters or []
                        self.body = body or ""
                        self.annotations = annotations or []

            class CodeAnalyzer(ast.NodeVisitor):
                def __init__(self):
                    self.file_structure = PythonFileStructure()
                    self.function_calls = defaultdict(set)
                    self.source_code = ""

                def visit_Import(self, node):
                    for alias in node.names:
                        self.file_structure.imports.append(alias.name)
                    self.generic_visit(node)

                def visit_ImportFrom(self, node):
                    module = node.module
                    for alias in node.names:
                        self.file_structure.imports.append(f"{module}.{alias.name}")
                    self.generic_visit(node)

                def visit_ClassDef(self, node):
                    class_name = node.name
                    extended_classes = [base.id for base in node.bases if isinstance(base, ast.Name)]
                    annotations = [decorator.id for decorator in node.decorator_list if isinstance(decorator, ast.Name)]
                    new_class = PythonFileStructure.PythonClassStructure(class_name, extended_classes, annotations)
                    self.file_structure.classes.append(new_class)
                    self.generic_visit(node)

                def visit_FunctionDef(self, node):
                    func_name = node.name
                    args = [arg.arg for arg in node.args.args]
                    return_type = ast.unparse(node.returns) if node.returns else ""
                    function_body = ast.get_source_segment(self.source_code, node)
                    annotations = [decorator.id for decorator in node.decorator_list if isinstance(decorator, ast.Name)]
                    new_method = PythonFileStructure.PythonMethodStructure(func_name, return_type, args, function_body, annotations)

                    if self.file_structure.classes:
                        self.file_structure.classes[-1].methods.append(new_method)
                    else:
                        self.file_structure.functions.append(new_method)

                    for child in ast.walk(node):
                        if isinstance(child, ast.Call) and isinstance(child.func, ast.Name):
                            called_func = child.func.id
                            self.function_calls[func_name].add(called_func)

                    self.generic_visit(node)

                def analyze(self, source_code):
                    self.source_code = source_code
                    tree = ast.parse(self.source_code)
                    self.visit(tree)

                def report_json(self):
                    result = {
                        "imports": self.file_structure.imports,
                        "classes": [
                            {
                                "name": cls.name,
                                "extendedClasses": cls.extended_classes,
                                "annotations": cls.annotations,
                                "methods": [
                                    {
                                        "name": method.name,
                                        "returnType": method.return_type,
                                        "parameters": method.parameters,
                                        "body": method.body,
                                        "annotations": method.annotations,
                                    }
                                    for method in cls.methods
                                ],
                            }
                            for cls in self.file_structure.classes
                        ],
                        "functions": [
                            {
                                "name": func.name,
                                "returnType": func.return_type,
                                "parameters": func.parameters,
                                "body": func.body,
                                "annotations": func.annotations,
                            }
                            for func in self.file_structure.functions
                        ],
                        "function_dependencies": {func: list(called_funcs) for func, called_funcs in self.function_calls.items()}
                    }
                    return json.dumps(result, indent=4)

            if __name__ == "__main__":
                analyzer = CodeAnalyzer()

                if len(sys.argv) > 1:
                    filepath = sys.argv[1]
                    with open(filepath, "r", encoding="utf-8") as f:
                        source_code = f.read()
                else:
                    source_code = sys.stdin.read()

                analyzer.analyze(source_code)
                print(analyzer.report_json())
            """;

    static {
        // Find the first available Python executable
        PYTHON_EXECUTABLE = findPythonExecutable();
        if (PYTHON_EXECUTABLE == null) {
            System.err.println("Error: No Python executable found. Tried: " + PYTHON_EXECUTABLES);
        }
    }

    private static String findPythonExecutable() {
        for (String candidate : PYTHON_EXECUTABLES) {
            if (candidate == null) continue; // Skip null entries (e.g., unset env var)
            try {
                ProcessBuilder pb = new ProcessBuilder(candidate, "--version");
                Process p = pb.start();
                int exitCode = p.waitFor();
                if (exitCode == 0) {
                    System.out.println("Found Python executable: " + candidate);
                    return candidate;
                }
            } catch (IOException | InterruptedException e) {
                // Ignore and try the next candidate
            }
        }
        return null;
    }

    public static JsonNode parsePythonCode(String pythonCode) {
        if (PYTHON_EXECUTABLE == null) {
            throw new IllegalStateException("No Python executable found on the server. Cannot proceed.");
        }

        try {
            // Create a temporary file with the embedded Python script content
            File pythonScriptFile = createTempScriptFile();

            if (pythonScriptFile == null || !pythonScriptFile.exists()) {
                System.err.println("Error: Could not create temporary pythonAnalyze.py at " + pythonScriptFile);
                return null;
            }

            // Build the process to execute the Python script
            ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_EXECUTABLE, pythonScriptFile.getAbsolutePath());

            // Set the working directory to the application's runtime directory
            processBuilder.directory(new File(WORKING_DIR));

            // Redirect error stream to output
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Write the Python code to the process input stream
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(pythonCode);
                writer.flush();
            }

            // Read the output of the script
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            // Wait for process to complete and print exit code
            int exitCode = process.waitFor();
            System.out.println("pythonAnalyze.py exited with code: " + exitCode);

            // Parse the JSON output
            String jsonOutput = output.toString().trim();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonOutput);

            // Clean up the temporary file
            pythonScriptFile.deleteOnExit();

            return jsonNode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File createTempScriptFile() {
        try {
            // Create a temporary file for the Python script
            File tempFile = File.createTempFile("pythonAnalyze", ".py");

            // Write the embedded script content to the temporary file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(PYTHON_ANALYZE_SCRIPT);
            }

            System.out.println("Created temporary pythonAnalyze.py at: " + tempFile.getAbsolutePath());
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // For testing locally
    public static void main(String[] args) {
        String pythonCode = """
                def example_function(x):
                    if x > 0:
                        return "Positive"
                    elif x < 0:
                        return "Negative"
                    else:
                        return "Zero"
                """;
        JsonNode result = parsePythonCode(pythonCode);
        System.out.println("Parsed JSON: " + result);
    }
}