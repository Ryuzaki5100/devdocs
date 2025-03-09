package com.devdocs.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class SimpleJavaParser {

    public static void main(String[] args) {
        String javaCode = "package com.devdocs.demo.utils;\n" +
                "\n" +
                "import java.io.BufferedReader;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.net.HttpURLConnection;\n" +
                "import java.net.URL;\n" +
                "import java.util.Base64;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "import org.json.JSONArray;\n" +
                "import org.json.JSONObject;\n" +
                "\n" +
                "import io.github.cdimascio.dotenv.Dotenv;\n" +
                "\n" +
                "public class GitHubRepoContents {\n" +
                "\n" +
                "    static Dotenv dotenv = Dotenv.configure().load();\n" +
                "\n" +
                "    private static final String OWNER = \"Ryuzaki5100\";\n" +
                "    private static final String REPO = \"rbxcb\";\n" +
                "    private static final String BRANCH = \"master\"; // Specify the branch you want to access\n" +
                "    private static final String TOKEN = dotenv.get(\"PERSONAL_ACCESS_TOKEN\");\n" +
                "    private static final int RATE_LIMIT_DELAY = 1000; // Delay in milliseconds to avoid rate limiting\n" +
                "\n" +
                "    private static Map<String, Long> rateLimitHeaders = new HashMap<>();\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        try {\n" +
                "            // List the file structure\n" +
                "//            listFileStructure(OWNER, REPO, BRANCH, \"\");\n" +
                "\n" +
                "            // Retrieve and print the contents of a specific file\n" +
                "            String filePath = \"demo/src/main/java/com/devdocs/demo/DemoApplication.java\";\n" +
                "            String fileContents = getFileContents(OWNER, REPO, BRANCH, filePath);\n" +
                "//            System.out.println(fileContents);\n" +
                "//            System.out.println(\"Contents of \" + filePath + \":\");\n" +
                "//            System.out.println(fileContents);\n" +
                "            System.out.println(SimpleJavaParser.parseJavaCode(fileContents));\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static void listFileStructure(String owner, String repo, String branch, String path) throws Exception {\n" +
                "        String urlString = String.format(\"https://api.github.com/repos/%s/%s/contents/%s?ref=%s\", owner, repo, path, branch);\n" +
                "        URL url = new URL(urlString);\n" +
                "        HttpURLConnection connection = (HttpURLConnection) url.openConnection();\n" +
                "        connection.setRequestMethod(\"GET\");\n" +
                "        connection.setRequestProperty(\"Authorization\", \"token \" + TOKEN);\n" +
                "        connection.setRequestProperty(\"Accept\", \"application/vnd.github.v3+json\");\n" +
                "\n" +
                "        checkRateLimit(connection);\n" +
                "\n" +
                "        int responseCode = connection.getResponseCode();\n" +
                "        if (responseCode == 200) {\n" +
                "            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));\n" +
                "            String inputLine;\n" +
                "            StringBuilder content = new StringBuilder();\n" +
                "            while ((inputLine = in.readLine()) != null) {\n" +
                "                content.append(inputLine);\n" +
                "            }\n" +
                "            in.close();\n" +
                "            connection.disconnect();\n" +
                "\n" +
                "            JSONArray contents = new JSONArray(content.toString());\n" +
                "            for (int i = 0; i < contents.length(); i++) {\n" +
                "                JSONObject item = contents.getJSONObject(i);\n" +
                "                if (item.getString(\"type\").equals(\"file\")) {\n" +
                "                    System.out.println(\"File: \" + item.getString(\"path\"));\n" +
                "                } else if (item.getString(\"type\").equals(\"dir\")) {\n" +
                "                    System.out.println(\"Directory: \" + item.getString(\"path\"));\n" +
                "                    listFileStructure(owner, repo, branch, item.getString(\"path\"));\n" +
                "                }\n" +
                "            }\n" +
                "        } else {\n" +
                "            handleErrorResponse(connection);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static String getFileContents(String owner, String repo, String branch, String path) throws Exception {\n" +
                "        String urlString = String.format(\"https://api.github.com/repos/%s/%s/contents/%s?ref=%s\", owner, repo, path, branch);\n" +
                "        URL url = new URL(urlString);\n" +
                "        HttpURLConnection connection = (HttpURLConnection) url.openConnection();\n" +
                "        connection.setRequestMethod(\"GET\");\n" +
                "        connection.setRequestProperty(\"Authorization\", \"token \" + TOKEN);\n" +
                "        connection.setRequestProperty(\"Accept\", \"application/vnd.github.v3+json\");\n" +
                "\n" +
                "        checkRateLimit(connection);\n" +
                "\n" +
                "        int responseCode = connection.getResponseCode();\n" +
                "        if (responseCode == 200) {\n" +
                "            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));\n" +
                "            String inputLine;\n" +
                "            StringBuilder content = new StringBuilder();\n" +
                "            while ((inputLine = in.readLine()) != null) {\n" +
                "                content.append(inputLine);\n" +
                "            }\n" +
                "            in.close();\n" +
                "            connection.disconnect();\n" +
                "\n" +
                "            JSONObject fileJson = new JSONObject(content.toString());\n" +
                "            String encodedContent = fileJson.getString(\"content\").replaceAll(\"\\\\n\", \"\");\n" +
                "            return new String(Base64.getDecoder().decode(encodedContent));\n" +
                "        } else {\n" +
                "            handleErrorResponse(connection);\n" +
                "            return null;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static void checkRateLimit(HttpURLConnection connection) {\n" +
                "        if (rateLimitHeaders.containsKey(\"X-RateLimit-Remaining\") && rateLimitHeaders.get(\"X-RateLimit-Remaining\") == 0) {\n" +
                "            try {\n" +
                "                Thread.sleep(RATE_LIMIT_DELAY);\n" +
                "            } catch (InterruptedException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static void handleErrorResponse(HttpURLConnection connection) throws Exception {\n" +
                "        int responseCode = connection.getResponseCode();\n" +
                "        System.out.println(\"Failed to retrieve contents: \" + responseCode);\n" +
                "        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));\n" +
                "        String inputLine;\n" +
                "        StringBuilder content = new StringBuilder();\n" +
                "        while ((inputLine = in.readLine()) != null) {\n" +
                "            content.append(inputLine);\n" +
                "        }\n" +
                "        in.close();\n" +
                "        connection.disconnect();\n" +
                "        System.out.println(content.toString());\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n";

        System.out.println(SimpleJavaParser.parseCodeToJSON(javaCode));
    }

    public static JavaFileStructure parseJavaCode(String javaCode) {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaCode);
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            CompilationUnit cu = parseResult.getResult().get();
            JavaFileVisitor visitor = new JavaFileVisitor();
            cu.accept(visitor, null);
            return visitor.getJavaFileStructure();
        } else {
            System.err.println("Parsing failed: " + parseResult.getProblems());
            return null;
        }
    }

    public static String parseCodeToJSON(String fileContent) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print
        try {
            JavaFileStructure parsedStructure = SimpleJavaParser.parseJavaCode(fileContent);
            if (parsedStructure == null) {
                return objectMapper.writeValueAsString(Map.of("error", "Parsing failed"));
            }
            return objectMapper.writeValueAsString(parsedStructure);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return objectMapper.writeValueAsString(Map.of("error", "Exception occurred: " + e.getMessage()));
            } catch (Exception ex) {
                return "{\"error\": \"Critical JSON serialization failure\"}";
            }
        }
    }

    public static class JavaFileStructure {
        public List<String> imports = new ArrayList<>();
        public List<JavaClassStructure> classes = new ArrayList<>();

        public static class JavaClassStructure {
            public String name = "";
            public List<String> annotations = new ArrayList<>();
            public String extendedClass = "";
            public List<String> implementedInterfaces = new ArrayList<>();
            public List<JavaFieldStructure> fields = new ArrayList<>();
            public List<JavaMethodStructure> methods = new ArrayList<>();
            public List<JavaClassStructure> nestedClasses = new ArrayList<>(); // For nested/local classes
        }

        public static class JavaFieldStructure {
            public String name = "";
            public String type = "";
            public List<String> annotations = new ArrayList<>();
        }

        public static class JavaMethodStructure {
            public String name = "";
            public String returnType = "";
            public List<String> annotations = new ArrayList<>();
            public List<String> parameters = new ArrayList<>();
            public String body = "";
        }
    }

    private static class JavaFileVisitor extends VoidVisitorAdapter<Void> {
        private final JavaFileStructure javaFileStructure = new JavaFileStructure();
        private final Stack<JavaFileStructure.JavaClassStructure> classStack = new Stack<>();
        private final Stack<JavaFileStructure.JavaMethodStructure> methodStack = new Stack<>();

        @Override
        public void visit(ImportDeclaration n, Void arg) {
            javaFileStructure.imports.add(n.getNameAsString());
            super.visit(n, arg);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            JavaFileStructure.JavaClassStructure newClass = new JavaFileStructure.JavaClassStructure();
            newClass.name = n.getNameAsString();
            newClass.annotations = extractAnnotations(n);
            newClass.extendedClass = n.getExtendedTypes().isNonEmpty() ? n.getExtendedTypes().get(0).getNameAsString() : "";
            newClass.implementedInterfaces = n.getImplementedTypes().stream().map(t -> t.getNameAsString()).toList();

            // Determine if this is a top-level, nested, or local class
            if (classStack.isEmpty()) {
                javaFileStructure.classes.add(newClass); // Top-level class
            } else if (!methodStack.isEmpty()) {
                // Local class within a method
                methodStack.peek().body += "\n[Local class: " + newClass.name + "]";
                classStack.peek().nestedClasses.add(newClass); // Still nest it for structure
            } else {
                // Nested class within another class
                classStack.peek().nestedClasses.add(newClass);
            }

            classStack.push(newClass);
            super.visit(n, arg);
            classStack.pop();
        }

        @Override
        public void visit(FieldDeclaration n, Void arg) {
            if (classStack.isEmpty()) {
                System.err.println("Warning: FieldDeclaration found but no class exists in the structure.");
                return;
            }
            JavaFileStructure.JavaClassStructure currentClass = classStack.peek();
            for (VariableDeclarator var : n.getVariables()) {
                JavaFileStructure.JavaFieldStructure field = new JavaFileStructure.JavaFieldStructure();
                field.name = var.getNameAsString();
                field.type = var.getType().toString();
                field.annotations = extractAnnotations(n);
                currentClass.fields.add(field);
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (classStack.isEmpty()) {
                System.err.println("Warning: MethodDeclaration found but no class exists in the structure.");
                return;
            }
            JavaFileStructure.JavaClassStructure currentClass = classStack.peek();
            JavaFileStructure.JavaMethodStructure method = new JavaFileStructure.JavaMethodStructure();
            method.name = n.getNameAsString();
            method.returnType = n.getType().toString();
            method.annotations = extractAnnotations(n);
            method.parameters = n.getParameters().stream()
                    .map(param -> param.getType() + " " + param.getNameAsString())
                    .toList();
            method.body = n.getBody().map(Object::toString).orElse("");
            currentClass.methods.add(method);

            methodStack.push(method);
            super.visit(n, arg);
            methodStack.pop();
        }

        private List<String> extractAnnotations(NodeWithAnnotations<?> node) {
            return node.getAnnotations().stream().map(a -> a.getNameAsString()).toList();
        }

        public JavaFileStructure getJavaFileStructure() {
            return javaFileStructure;
        }
    }
}