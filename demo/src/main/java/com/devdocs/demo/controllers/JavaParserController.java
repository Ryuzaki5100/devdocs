package com.devdocs.demo.controllers;

import com.devdocs.demo.utils.GitHubRepoContents;
import com.devdocs.demo.utils.SimpleJavaParser;
import com.devdocs.demo.utils.SimpleJavaParser.JavaFileStructure;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavaParserController {
    @GetMapping("/parseJavaCodeToJSON")
    public JavaFileStructure parseJavaCodeToJSON(@RequestParam String owner, @RequestParam String repo, @RequestParam String branch, @RequestParam String filePath) throws Exception {
        System.out.println(filePath);
        String fileContent = GitHubRepoContents.getFileContents(owner, repo, branch, filePath);
        return SimpleJavaParser.parseJavaCode(fileContent);
    }
}
