package com.devdocs.demo.controllers;

import com.devdocs.demo.utils.CombinedPythonParser;
import com.devdocs.demo.utils.GitHubRepoContents;
import com.devdocs.demo.utils.PythonParser;
import com.devdocs.demo.utils.SimpleJavaParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ParserController {
    @GetMapping("/parseCode")
    public JsonNode parseCode(@RequestParam String owner, @RequestParam String repo, @RequestParam String branch, @RequestParam String filePath) throws Exception {
        System.out.println(filePath.length());
        filePath = filePath.trim();
        System.out.println(filePath.length());

        String[] temp = filePath.split("\\.");
        String extension = temp[temp.length - 1];

        if (extension.equals("java"))
            return SimpleJavaParser.parseJavaCode(GitHubRepoContents.getFileContents(owner, repo, branch, filePath));

        else if (extension.equals("py"))
            return CombinedPythonParser.parsePythonCode(GitHubRepoContents.getFileContents(owner, repo, branch, filePath));

        else
            return null;
    }
}
