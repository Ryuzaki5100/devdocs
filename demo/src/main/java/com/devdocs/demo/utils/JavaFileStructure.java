package com.devdocs.demo.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JavaFileStructure {
    List<String> imports = new ArrayList<>();
    List<JavaClassStructure> classes = new ArrayList<>();


    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class JavaClassStructure {
        String name;
        String extendedClass;
        List<String> implementedInterfaces = new ArrayList<>();
        List<String> annotations = new ArrayList<>();
        List<JavaFieldStructure> fields = new ArrayList<>();
        List<JavaMethodStructure> methods = new ArrayList<>();
    }


    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class JavaFieldStructure {
        String name;
        String type;
        List<String> annotations = new ArrayList<>();
    }


    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class JavaMethodStructure {
        String name;
        String returnType;
        List<String> parameters = new ArrayList<>();
        List<String> annotations = new ArrayList<>();
        String body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Imports:\n");
        imports.forEach(i -> sb.append("  ").append(i).append("\n"));

        for (JavaClassStructure cls : classes) {
            sb.append("\nClass: ").append(cls.name).append("\n");
            if (!cls.annotations.isEmpty()) {
                sb.append("  Annotations: ").append(cls.annotations).append("\n");
            }
            if (!cls.extendedClass.isEmpty()) {
                sb.append("  Extends: ").append(cls.extendedClass).append("\n");
            }
            if (!cls.implementedInterfaces.isEmpty()) {
                sb.append("  Implements: ").append(cls.implementedInterfaces).append("\n");
            }
            sb.append("  Fields:\n");
            for (JavaFieldStructure field : cls.fields) {
                sb.append("    - ").append(field.type).append(" ").append(field.name);
                if (!field.annotations.isEmpty()) {
                    sb.append(" (Annotations: ").append(field.annotations).append(")");
                }
                sb.append("\n");
            }
            sb.append("  Methods:\n");
            for (JavaMethodStructure method : cls.methods) {
                sb.append("    - ").append(method.returnType).append(" ").append(method.name)
                        .append("(").append(String.join(", ", method.parameters)).append(")\n");
                if (!method.annotations.isEmpty()) {
                    sb.append("      Annotations: ").append(method.annotations).append("\n");
                }
                sb.append("      Body: ").append(method.body).append("\n");
            }
        }
        return sb.toString();
    }

}
