package com.devdocs.demo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class GitHubRepoContents {

    static Dotenv dotenv = Dotenv.configure().load();

    private static final String OWNER = "Ryuzaki5100";
    private static final String REPO = "rbxcb";
    private static final String BRANCH = "master"; // Specify the branch you want to access
    private static final String TOKEN = "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";
    private static final int RATE_LIMIT_DELAY = 1000; // Delay in milliseconds to avoid rate limiting

    private static Map<String, Long> rateLimitHeaders = new HashMap<>();

    public static void main(String[] args) {
        try {
            // List the file structure
//            listFileStructure(OWNER, REPO, BRANCH, "");

            // Retrieve and print the contents of a specific file
            String filePath = "demo/src/main/java/com/devdocs/demo/DemoApplication.java";
            String fileContents = getFileContents(OWNER, REPO, BRANCH, filePath);
//            System.out.println(fileContents);
//            System.out.println("Contents of " + filePath + ":");
//            System.out.println(fileContents);
            System.out.println(SimpleJavaParser.parseJavaCode(fileContents));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listFileStructure(String owner, String repo, String branch, String path) throws Exception {
        String urlString = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s", owner, repo, path, branch);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + TOKEN);
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        checkRateLimit(connection);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            JSONArray contents = new JSONArray(content.toString());
            for (int i = 0; i < contents.length(); i++) {
                JSONObject item = contents.getJSONObject(i);
                if (item.getString("type").equals("file")) {
                    System.out.println("File: " + item.getString("path"));
                } else if (item.getString("type").equals("dir")) {
                    System.out.println("Directory: " + item.getString("path"));
                    listFileStructure(owner, repo, branch, item.getString("path"));
                }
            }
        } else {
            handleErrorResponse(connection);
        }
    }

    public static String getFileContents(String owner, String repo, String branch, String path) throws Exception {
        String urlString = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s", owner, repo, path, branch);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + TOKEN);
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        checkRateLimit(connection);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            JSONObject fileJson = new JSONObject(content.toString());
            String encodedContent = fileJson.getString("content").replaceAll("\\n", "");
            return new String(Base64.getDecoder().decode(encodedContent));
        } else {
            handleErrorResponse(connection);
            return null;
        }
    }

    private static void checkRateLimit(HttpURLConnection connection) {
        if (rateLimitHeaders.containsKey("X-RateLimit-Remaining") && rateLimitHeaders.get("X-RateLimit-Remaining") == 0) {
            try {
                Thread.sleep(RATE_LIMIT_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleErrorResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        System.out.println("Failed to retrieve contents: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        System.out.println(content.toString());
    }


}
