package com.devdocs.demo.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CustomPython3Lexer extends Python3Lexer {
    private Stack<Integer> indentLevels = new Stack<>();
    private Queue<Token> pendingTokens = new LinkedList<>();
    private int currentIndent = 0;

    public CustomPython3Lexer(CharStream input) {
        super(input);
        indentLevels.push(0); // Initial indent level is 0
    }

    private void handleNewline() {
        // Emit the NEWLINE token
        emit(new CommonToken(NEWLINE, "\n"));

        // Read the indentation (spaces) at the start of the next line
        int nextChar = _input.LA(1);
        int spaces = 0;
        while (nextChar == ' ' || nextChar == '\t') {
            spaces += (nextChar == '\t') ? 4 : 1; // Treat tabs as 4 spaces (adjust as needed)
            _input.consume();
            nextChar = _input.LA(1);
        }

        // Skip if the line is empty, a comment, or EOF
        if (nextChar == '\r' || nextChar == '\n' || nextChar == '#' || nextChar == CharStream.EOF) {
            return;
        }

        // Compare the new indent level with the current stack
        int currentLevel = indentLevels.peek();
        if (spaces > currentLevel) {
            // Indentation increased: emit INDENT
            indentLevels.push(spaces);
            pendingTokens.add(new CommonToken(INDENT, "<INDENT>"));
        } else if (spaces < currentLevel) {
            // Indentation decreased: emit DEDENT(s)
            while (!indentLevels.isEmpty() && spaces < indentLevels.peek()) {
                indentLevels.pop();
                pendingTokens.add(new CommonToken(DEDENT, "<DEDENT>"));
            }
            if (!indentLevels.isEmpty() && spaces != indentLevels.peek()) {
                throw new RuntimeException("Indentation error: unindent does not match any outer indentation level");
            }
        }
        // If spaces == currentLevel, no INDENT/DEDENT needed
    }

    @Override
    public Token nextToken() {
        // If there are pending tokens (INDENT/DEDENT), return them first
        if (!pendingTokens.isEmpty()) {
            return pendingTokens.poll();
        }

        Token next = super.nextToken();

        // Call handleNewline() when a NEWLINE token is encountered
        if (next.getType() == NEWLINE) {
            handleNewline();
        }

        // At EOF, emit DEDENT tokens to close all open blocks
        if (next.getType() == EOF) {
            while (indentLevels.peek() > 0) {
                indentLevels.pop();
                pendingTokens.add(new CommonToken(DEDENT, "<DEDENT>"));
            }
        }

        return pendingTokens.isEmpty() ? next : pendingTokens.poll();
    }
}