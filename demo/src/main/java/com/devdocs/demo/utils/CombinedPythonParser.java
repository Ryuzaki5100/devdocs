package com.devdocs.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CombinedPythonParser {
    public static JsonNode parsePythonCode(String pythonCode) {
        try {
            // Parse using PythonDAGFileParser
            JsonNode dagResult = PythonDAGFileParser.parsePythonCode(pythonCode);

            // Parse using PythonParser
            JsonNode structureResult = PythonParser.parsePythonCode(pythonCode);

            // Merge results
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode combinedResult = objectMapper.createObjectNode();

            if (structureResult != null) {
                combinedResult.set("imports", structureResult.get("imports"));
                combinedResult.set("classes", structureResult.get("classes"));
                combinedResult.set("functions", structureResult.get("functions"));
                combinedResult.set("function_dependencies", structureResult.get("function_dependencies"));
            }

            if (dagResult != null) {
                combinedResult.set("DAG", dagResult.get("DAG"));
            }

            return combinedResult;
        } catch (Exception e) {
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
        System.out.println("Combined Parsed JSON: " + result);
    }
}
