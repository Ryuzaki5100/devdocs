package com.devdocs.demo.controllers;

import com.devdocs.demo.utils.*;
//import com.devdocs.demo.utils.SimplePythonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class PythonParserController {
    @GetMapping("/parsePythonCodeToJSON")
    public JsonNode parsePythonCodeToJSON(@RequestParam String owner, @RequestParam String repo, @RequestParam String branch, @RequestParam String filePath) throws Exception {
        System.out.println(filePath.length());
        filePath = filePath.trim();
        System.out.println(filePath.length());
        String fileContent = GitHubRepoContents.getFileContents(owner, repo, branch, filePath);
        return CombinedPythonParser.parsePythonCode(fileContent);
    }

    @PostMapping("/rawParse")
    public JsonNode rawParse(@RequestBody String fileContent) throws Exception {
        return CombinedPythonParser.parsePythonCode(fileContent);
    }
}
