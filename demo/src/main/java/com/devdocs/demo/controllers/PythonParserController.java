package com.devdocs.demo.controllers;

import com.devdocs.demo.utils.GitHubRepoContents;
import com.devdocs.demo.utils.SimpleJavaParser;
import com.devdocs.demo.utils.SimplePythonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PythonParserController {
    @GetMapping("/parsePythonCodeToJSON")
    public JsonNode parsePythonCodeToJSON(@RequestParam String owner, @RequestParam String repo, @RequestParam String branch, @RequestParam String filePath) throws Exception {
        System.out.println(filePath.length());
        filePath = filePath.trim();
        System.out.println(filePath.length());
        String fileContent = GitHubRepoContents.getFileContents(owner, repo, branch, filePath);
        return SimplePythonParser.parsePythonCode(fileContent);
    }
}
