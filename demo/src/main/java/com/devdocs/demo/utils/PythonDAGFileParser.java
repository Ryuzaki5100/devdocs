package com.devdocs.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PythonDAGFileParser {
    // Configurable Python executable
    private static final String PYTHON_EXECUTABLE = System.getenv("PYTHON_EXECUTABLE") != null ?
            System.getenv("PYTHON_EXECUTABLE") : "python3";

    // Simplified Python script to extract only DAG dependency chains
    private static final String PYTHON_ANALYZE_SCRIPT = """
            import ast
            import json
            import sys
            
            class DAGParser(ast.NodeVisitor):
                def __init__(self):
                    self.dependencies = []  # Store dependency chains as strings
            
                def visit_BinOp(self, node):
                    if isinstance(node.op, ast.RShift):
                        chain = self._build_dependency_chain(node)
                        if chain:
                            self.dependencies.append(chain)
                    self.generic_visit(node)
            
                def _build_dependency_chain(self, node):
                    if isinstance(node, ast.BinOp) and isinstance(node.op, ast.RShift):
                        left = self._build_dependency_chain(node.left)
                        right = self._build_dependency_chain(node.right)
                        if left and right:
                            return f"{left} >> {right}"
                        return ""
                    elif isinstance(node, ast.Name):
                        return node.id
                    elif isinstance(node, (ast.List, ast.Tuple)):
                        task_names = [elt.id for elt in node.elts if isinstance(elt, ast.Name)]
                        if task_names:
                            return f"[{', '.join(task_names)}]"
                        return ""
                    return ""
            
                def report_json(self):
                    return {
                        "DAG": self.dependencies
                    }
            
            if __name__ == "__main__":
                parser = DAGParser()
            
                if len(sys.argv) > 1:
                    filepath = sys.argv[1]
                    with open(filepath, "r", encoding="utf-8") as f:
                        source_code = f.read()
                else:
                    source_code = sys.stdin.read()
            
                tree = ast.parse(source_code)
                parser.visit(tree)
                print(json.dumps(parser.report_json(), indent=4))
            """;

    public static JsonNode parsePythonCode(String pythonCode) {
        try {
            // Write Python script to a temporary file
            File tempScript = File.createTempFile("python_analyze", ".py");
            try (BufferedWriter scriptWriter = new BufferedWriter(new FileWriter(tempScript))) {
                scriptWriter.write(PYTHON_ANALYZE_SCRIPT);
                scriptWriter.flush();
            }

            // Build the process to execute the Python script
            List<String> command = new ArrayList<>();
            command.add(PYTHON_EXECUTABLE);
            command.add(tempScript.getAbsolutePath());

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Write the Python code to be analyzed to the process input stream
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(pythonCode);
                writer.flush();
            }

            // Read the output of the script
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Capture error output for debugging
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }

            // Wait for process to complete
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);
            if (errorOutput.length() > 0) {
                System.err.println("Error output: " + errorOutput.toString());
            }

            // Delete the temporary script file
            tempScript.delete();

            // Parse the JSON output
            String jsonOutput = output.toString().trim();
            if (jsonOutput.isEmpty()) {
                System.err.println("Python script returned empty output");
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonOutput);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    // For testing locally
    public static void main(String[] args) {
        String pythonCode = """
                import datetime
                import pendulum
                from airflow.models.dag import DAG
                from airflow.operators.empty import EmptyOperator
                from airflow.operators.latest_only import LatestOnlyOperator
                from airflow.utils.trigger_rule import TriggerRule
                
                with DAG(
                    dag_id="latest_only_with_trigger",
                    schedule=datetime.timedelta(hours=4),
                    start_date=pendulum.datetime(2021, 1, 1, tz="UTC"),
                    catchup=False,
                    tags=["example3"],
                ) as dag:
                    latest_only = LatestOnlyOperator(task_id="latest_only")
                    task1 = EmptyOperator(task_id="task1")
                    task2 = EmptyOperator(task_id="task2")
                    task3 = EmptyOperator(task_id="task3")
                    task4 = EmptyOperator(task_id="task4", trigger_rule=TriggerRule.ALL_DONE)
                
                    latest_only >> task1 >> [task3, task4]
                    task2 >> [task3, task4]
                """;
        JsonNode result = parsePythonCode(pythonCode);
        System.out.println("Parsed JSON: " + result);
    }
}