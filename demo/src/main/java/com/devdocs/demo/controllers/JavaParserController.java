package com.devdocs.demo.controllers;

import com.devdocs.demo.utils.GitHubRepoContents;
import com.devdocs.demo.utils.SimpleJavaParser;
import com.devdocs.demo.utils.SimpleJavaParser.JavaFileStructure;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavaParserController {
    @GetMapping("/parseJavaCodeToJSON")
    public JsonNode parseJavaCodeToJSON(@RequestParam String owner, @RequestParam String repo, @RequestParam String branch, @RequestParam String filePath) throws Exception {
//        int x = (int) filePath.charAt(filePath.length() - 1);
//        System.out.println(x);
        System.out.println(filePath.length());
        filePath = filePath.trim();
        System.out.println(filePath.length());
        String fileContent = GitHubRepoContents.getFileContents(owner, repo, branch, filePath);
        return SimpleJavaParser.parseJavaCode(fileContent);
//        return null;
    }
}
