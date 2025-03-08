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
        String javaCode = "package com.cube.demo.rbxcb.rbxcb_3x3x3.Model;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import java.util.concurrent.*;\n" +
                "import java.util.concurrent.atomic.AtomicReference;\n" +
                "\n" +
                "public class Cube implements Cloneable {\n" +
                "\n" +
                "    private static final Map<Character, EdgePos> nextEdgePos = Map.of(\n" +
                "            'R', new EdgePos(new byte[]{0, 5, 2, 3, 4, 9, 1, 7, 8, 6, 10, 11}),\n" +
                "            'U', new EdgePos(new byte[]{1, 2, 3, 0, 4, 5, 6, 7, 8, 9, 10, 11}),\n" +
                "            'F', new EdgePos(new byte[]{0, 1, 6, 3, 4, 5, 10, 2, 8, 9, 7, 11}),\n" +
                "            'B', new EdgePos(new byte[]{4, 1, 2, 3, 8, 0, 6, 7, 5, 9, 10, 11}),\n" +
                "            'L', new EdgePos(new byte[]{0, 1, 2, 7, 3, 5, 6, 11, 8, 9, 10, 4}),\n" +
                "            'D', new EdgePos(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 11, 8, 9, 10})\n" +
                "    );\n" +
                "\n" +
                "    private static final Map<Character, CornerPos> nextCornerPos = Map.of(\n" +
                "            'R', new CornerPos(new byte[]{0, 5, 1, 3, 4, 6, 2, 7}),\n" +
                "            'U', new CornerPos(new byte[]{1, 2, 3, 0, 4, 5, 6, 7}),\n" +
                "            'F', new CornerPos(new byte[]{0, 1, 6, 2, 4, 5, 7, 3}),\n" +
                "            'B', new CornerPos(new byte[]{4, 0, 2, 3, 5, 1, 6, 7}),\n" +
                "            'L', new CornerPos(new byte[]{3, 1, 2, 7, 0, 5, 6, 4}),\n" +
                "            'D', new CornerPos(new byte[]{0, 1, 2, 3, 7, 4, 5, 6})\n" +
                "    );\n" +
                "\n" +
                "    private static final Map<Character, List<Map<Byte, Byte>>> nextEdgeOrientation = Map.of(\n" +
                "            'R', List.of(\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 2, (byte) 2),\n" +
                "                    Map.of((byte) 3, (byte) 2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) -3, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) 3, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -3, (byte) -2),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -1, (byte) -1)\n" +
                "            ),\n" +
                "            'U', List.of(\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 2, (byte) 1),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 1, (byte) -2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -2, (byte) -1),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -1, (byte) 2),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -1, (byte) -1)\n" +
                "            ),\n" +
                "            'F', List.of(\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 2, (byte) 2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) 3, (byte) 1, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) 1, (byte) -3),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) -1, (byte) 3),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -3, (byte) -1, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -1, (byte) -1)\n" +
                "            ),\n" +
                "            'B', List.of(\n" +
                "                    Map.of((byte) 3, (byte) -1, (byte) 2, (byte) 2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -1, (byte) -3),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) 1, (byte) 3),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -3, (byte) 1),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -1, (byte) -1)\n" +
                "            ),\n" +
                "            'L', List.of(\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 2, (byte) 2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) 3, (byte) -2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 3, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -3, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) -3, (byte) 2, (byte) -1, (byte) -1)\n" +
                "            ),\n" +
                "            'D', List.of(\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 2, (byte) 2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -2, (byte) -2),\n" +
                "                    Map.of((byte) 3, (byte) 3, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) 2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) 1, (byte) 1),\n" +
                "                    Map.of((byte) -2, (byte) -2, (byte) -1, (byte) -1),\n" +
                "                    Map.of((byte) 2, (byte) -1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -2, (byte) 1),\n" +
                "                    Map.of((byte) -3, (byte) -3, (byte) -1, (byte) -2)\n" +
                "            )\n" +
                "    );\n" +
                "\n" +
                "    private static final Map<Character, List<Map<Byte, Byte>>> nextCornerOrientation = Map.of(\n" +
                "            'R', List.of(\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) -3, (byte) 3, (byte) 2),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) 3, (byte) 3, (byte) 2),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) -3, (byte) -3, (byte) -2),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) 3, (byte) -3, (byte) -2),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -2, (byte) -3, (byte) -3)\n" +
                "            ),\n" +
                "            'U', List.of(\n" +
                "                    Map.of((byte) -1, (byte) 2, (byte) 2, (byte) 1, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) -2, (byte) 2, (byte) 1, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) -2, (byte) -2, (byte) -1, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) 2, (byte) -2, (byte) -1, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) -2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -2, (byte) -3, (byte) -3)\n" +
                "            ),\n" +
                "            'F', List.of(\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) -3, (byte) -2, (byte) -2, (byte) 3, (byte) 1),\n" +
                "                    Map.of((byte) -1, (byte) 3, (byte) -2, (byte) -2, (byte) 3, (byte) 1),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) -3, (byte) -2, (byte) -2, (byte) -3, (byte) -1),\n" +
                "                    Map.of((byte) -1, (byte) 3, (byte) -2, (byte) -2, (byte) -3, (byte) -1)\n" +
                "            ),\n" +
                "            'B', List.of(\n" +
                "                    Map.of((byte) -1, (byte) -3, (byte) 2, (byte) 2, (byte) 3, (byte) -1),\n" +
                "                    Map.of((byte) 1, (byte) 3, (byte) 2, (byte) 2, (byte) 3, (byte) -1),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) -2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -3, (byte) 2, (byte) 2, (byte) -3, (byte) 1),\n" +
                "                    Map.of((byte) 1, (byte) 3, (byte) 2, (byte) 2, (byte) -3, (byte) 1),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) -2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -2, (byte) -3, (byte) -3)\n" +
                "            ),\n" +
                "            'L', List.of(\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 3, (byte) 3, (byte) -2),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) -2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -3, (byte) 3, (byte) -2),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 3, (byte) -3, (byte) 2),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) -2, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -3, (byte) -3, (byte) 2)\n" +
                "            ),\n" +
                "            'D', List.of(\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) 1, (byte) 1, (byte) -2, (byte) -2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -1, (byte) -2, (byte) -2, (byte) 3, (byte) 3),\n" +
                "                    Map.of((byte) -1, (byte) -2, (byte) 2, (byte) -1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 2, (byte) 2, (byte) -1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) 1, (byte) 2, (byte) -2, (byte) 1, (byte) -3, (byte) -3),\n" +
                "                    Map.of((byte) -1, (byte) -2, (byte) -2, (byte) 1, (byte) -3, (byte) -3)\n" +
                "            )\n" +
                "    );\n" +
                "\n" +
                "    private static final byte[][] edgeList = {\n" +
                "            {1, 37},\n" +
                "            {5, 28},\n" +
                "            {7, 19},\n" +
                "            {3, 10},\n" +
                "            {12, 41},\n" +
                "            {32, 39},\n" +
                "            {23, 30},\n" +
                "            {14, 21},\n" +
                "            {43, 52},\n" +
                "            {34, 50},\n" +
                "            {25, 46},\n" +
                "            {16, 48}\n" +
                "    };\n" +
                "\n" +
                "    private static final byte[][] cornerList = {\n" +
                "            {0, 9, 38},\n" +
                "            {2, 29, 36},\n" +
                "            {8, 20, 27},\n" +
                "            {6, 11, 18},\n" +
                "            {15, 44, 51},\n" +
                "            {35, 42, 53},\n" +
                "            {26, 33, 47},\n" +
                "            {17, 24, 45}\n" +
                "    };\n" +
                "\n" +
                "    private static final Map<Character, Byte> binEncoding = Map.of(\n" +
                "            'U', (byte) 0b100000,\n" +
                "            'L', (byte) 0b010000,\n" +
                "            'F', (byte) 0b001000,\n" +
                "            'R', (byte) 0b000100,\n" +
                "            'B', (byte) 0b000010,\n" +
                "            'D', (byte) 0b000001\n" +
                "    );\n" +
                "\n" +
                "    private static final Map<Character, Byte> priority = Map.of(\n" +
                "            'U', (byte) 2,\n" +
                "            'L', (byte) 0,\n" +
                "            'F', (byte) 1,\n" +
                "            'R', (byte) 0,\n" +
                "            'B', (byte) 1,\n" +
                "            'D', (byte) 2\n" +
                "    );\n" +
                "\n" +
                "    private static final Map<Byte, Byte> edgeNumberForPos;\n" +
                "    private static final Map<Byte, Byte> cornerNumberForPos;\n" +
                "\n" +
                "    static {\n" +
                "        Map<Byte, Byte> edgeMap = new HashMap<>();\n" +
                "        edgeMap.put((byte) 0b100010, (byte) 0);\n" +
                "        edgeMap.put((byte) 0b100100, (byte) 1);\n" +
                "        edgeMap.put((byte) 0b101000, (byte) 2);\n" +
                "        edgeMap.put((byte) 0b110000, (byte) 3);\n" +
                "        edgeMap.put((byte) 0b010010, (byte) 4);\n" +
                "        edgeMap.put((byte) 0b000110, (byte) 5);\n" +
                "        edgeMap.put((byte) 0b001100, (byte) 6);\n" +
                "        edgeMap.put((byte) 0b011000, (byte) 7);\n" +
                "        edgeMap.put((byte) 0b000011, (byte) 8);\n" +
                "        edgeMap.put((byte) 0b000101, (byte) 9);\n" +
                "        edgeMap.put((byte) 0b001001, (byte) 10);\n" +
                "        edgeMap.put((byte) 0b010001, (byte) 11);\n" +
                "\n" +
                "        edgeNumberForPos = Collections.unmodifiableMap(edgeMap);\n" +
                "\n" +
                "        Map<Byte, Byte> cornerMap = new HashMap<>();\n" +
                "        cornerMap.put((byte) 0b110010, (byte) 0);\n" +
                "        cornerMap.put((byte) 0b100110, (byte) 1);\n" +
                "        cornerMap.put((byte) 0b101100, (byte) 2);\n" +
                "        cornerMap.put((byte) 0b111000, (byte) 3);\n" +
                "        cornerMap.put((byte) 0b010011, (byte) 4);\n" +
                "        cornerMap.put((byte) 0b000111, (byte) 5);\n" +
                "        cornerMap.put((byte) 0b001101, (byte) 6);\n" +
                "        cornerMap.put((byte) 0b011001, (byte) 7);\n" +
                "\n" +
                "        cornerNumberForPos = Collections.unmodifiableMap(cornerMap);\n" +
                "    }\n" +
                "\n" +
                "    public static final int[][] edgePossiblePlacesStage3 = {\n" +
                "            {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},\n" +
                "            {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1},\n" +
                "            {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},\n" +
                "            {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1},\n" +
                "            {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},\n" +
                "            {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},\n" +
                "            {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},\n" +
                "            {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},\n" +
                "            {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},\n" +
                "            {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1},\n" +
                "            {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},\n" +
                "            {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1}};\n" +
                "\n" +
                "    public static final int[][] cornerPossiblePlacesStage3 = {\n" +
                "            {1, 0, 1, 0, 0, 1, 0, 1},\n" +
                "            {0, 1, 0, 1, 1, 0, 1, 0},\n" +
                "            {1, 0, 1, 0, 0, 1, 0, 1},\n" +
                "            {0, 1, 0, 1, 1, 0, 1, 0},\n" +
                "            {0, 1, 0, 1, 1, 0, 1, 0},\n" +
                "            {1, 0, 1, 0, 0, 1, 0, 1},\n" +
                "            {0, 1, 0, 1, 1, 0, 1, 0},\n" +
                "            {1, 0, 1, 0, 0, 1, 0, 1}};\n" +
                "\n" +
                "    private Edge edge;\n" +
                "    private Corner corner;\n" +
                "\n" +
                "    public Cube() {\n" +
                "        this.edge = new Edge();\n" +
                "        this.corner = new Corner();\n" +
                "    }\n" +
                "\n" +
                "    public Cube(Cube c) {\n" +
                "        this.setEdge(new Edge(c.getEdge()));\n" +
                "        this.setCorner(new Corner(c.getCorner()));\n" +
                "    }\n" +
                "\n" +
                "    public Cube(Edge edge, Corner corner) {\n" +
                "        this.edge = edge;\n" +
                "        this.corner = corner;\n" +
                "    }\n" +
                "\n" +
                "    public Cube(String colorInput) {\n" +
                "        Cube c = new Cube();\n" +
                "        EdgePos edgePos = c.getEdge().getEdgePos();\n" +
                "        EdgeOrientation edgeOrientation = c.getEdge().getEdgeOrientation();\n" +
                "        CornerPos cornerPos = c.getCorner().getCornerPos();\n" +
                "        CornerOrientation cornerOrientation = c.getCorner().getCornerOrientation();\n" +
                "        byte[] basicPositionsInfo = {4, 13, 22, 31, 40, 49}, basicOrientationInfo = {3, -1, -2, 1, 2, -3};\n" +
                "        String basicPositions = \"ULFRBD\";\n" +
                "        StringBuilder givenPositions = new StringBuilder();\n" +
                "        HashMap<Character, Character> colorToSide = new HashMap<>();\n" +
                "        for (int i = 0; i < basicOrientationInfo.length; i++)\n" +
                "            givenPositions.append(colorInput.charAt(basicPositionsInfo[i]));\n" +
                "        for (int i = 0; i < 6; i++)\n" +
                "            colorToSide.put(givenPositions.charAt(i), basicPositions.charAt(i));\n" +
                "        byte tempCounter = 0;\n" +
                "        for (byte[] bytes : edgeList) {\n" +
                "            char side1 = colorToSide.get(colorInput.charAt(bytes[0]));\n" +
                "            char side2 = colorToSide.get(colorInput.charAt(bytes[1]));\n" +
                "            byte binaryNum = (byte) (binEncoding.get(side1) ^ binEncoding.get(side2));\n" +
                "            edgePos.setVal(edgeNumberForPos.get(binaryNum), tempCounter++);\n" +
                "            byte priorityNumber = (byte) Math.max(priority.get(side1), priority.get(side2));\n" +
                "            byte referenceNumber = priorityNumber == priority.get(side1) ? bytes[0] : bytes[1];\n" +
                "            edgeOrientation.setVal(edgeNumberForPos.get(binaryNum), basicOrientationInfo[referenceNumber / 9]);\n" +
                "        }\n" +
                "        tempCounter = 0;\n" +
                "        for (byte[] bytes : cornerList) {\n" +
                "            char side1 = colorToSide.get(colorInput.charAt(bytes[0]));\n" +
                "            char side2 = colorToSide.get(colorInput.charAt(bytes[1]));\n" +
                "            char side3 = colorToSide.get(colorInput.charAt(bytes[2]));\n" +
                "            byte binaryNum = (byte) (binEncoding.get(side1) ^ binEncoding.get(side2) ^ binEncoding.get(side3));\n" +
                "            cornerPos.setVal(cornerNumberForPos.get(binaryNum), tempCounter++);\n" +
                "            byte priorityNumber = (byte) Math.max(priority.get(side1), Math.max(priority.get(side2), priority.get(side3)));\n" +
                "            byte referenceNumber = priorityNumber == priority.get(side1) ? bytes[0] : (priorityNumber == priority.get(side2) ? bytes[1] : bytes[2]);\n" +
                "            cornerOrientation.setVal(cornerNumberForPos.get((binaryNum)), basicOrientationInfo[referenceNumber / 9]);\n" +
                "        }\n" +
                "        this.setEdge(new Edge(edgePos, edgeOrientation));\n" +
                "        this.setCorner(new Corner(cornerPos, cornerOrientation));\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Cube clone() {\n" +
                "        return new Cube(this.getEdge().clone(), this.getCorner().clone());\n" +
                "    }\n" +
                "\n" +
                "    public static Cube execute(Cube c, String s) {\n" +
                "        Cube temp = c.clone();\n" +
                "        String[] moves = s.split(\" \");\n" +
                "        if (moves.length > 1) {\n" +
                "            StringBuilder sBuilder = new StringBuilder();\n" +
                "            for (String string : moves) {\n" +
                "                if (string.length() == 1)\n" +
                "                    sBuilder.append(string.charAt(0));\n" +
                "                else if (string.charAt(1) == '2')\n" +
                "                    sBuilder.append(String.valueOf(string.charAt(0)).repeat(2));\n" +
                "                else\n" +
                "                    sBuilder.append(String.valueOf(string.charAt(0)).repeat(3));\n" +
                "            }\n" +
                "            s = sBuilder.toString();\n" +
                "        }\n" +
                "        for (int i = 0; i < s.length(); i++) {\n" +
                "            char ch = s.charAt(i);\n" +
                "            EdgePos edgePos = temp.getEdge().getEdgePos().clone();\n" +
                "            EdgeOrientation edgeOrientation = temp.getEdge().getEdgeOrientation().clone();\n" +
                "            for (int j = 0; j < 12; j++) {\n" +
                "                edgeOrientation.setVal(j, nextEdgeOrientation.get(ch).get(edgePos.getVal()[j]).get(edgeOrientation.getVal()[j]));\n" +
                "                edgePos.setVal(j, nextEdgePos.get(ch).getVal()[edgePos.getVal()[j]]);\n" +
                "            }\n" +
                "            temp.setEdge(new Edge(edgePos, edgeOrientation));\n" +
                "            CornerPos cornerPos = temp.getCorner().getCornerPos().clone();\n" +
                "            CornerOrientation cornerOrientation = temp.getCorner().getCornerOrientation().clone();\n" +
                "            for (int j = 0; j < 8; j++) {\n" +
                "                cornerOrientation.setVal(j, nextCornerOrientation.get(ch).get(cornerPos.getVal()[j]).get(cornerOrientation.getVal()[j]));\n" +
                "                cornerPos.setVal(j, nextCornerPos.get(ch).getVal()[cornerPos.getVal()[j]]);\n" +
                "            }\n" +
                "            temp.setCorner(new Corner(cornerPos, cornerOrientation));\n" +
                "        }\n" +
                "        return temp;\n" +
                "    }\n" +
                "\n" +
                "    public static String reverseAlgorithm(String s) {\n" +
                "        StringBuilder result = new StringBuilder();\n" +
                "        for (int i = 0; i < s.length(); i++)\n" +
                "            result.append(String.valueOf(s.charAt(i)).repeat(3));\n" +
                "        return new StringBuilder(result.toString()).reverse().toString();\n" +
                "    }\n" +
                "\n" +
                "    public static ArrayList<String> getAlgorithm(String moves) {\n" +
                "        class Temp {\n" +
                "            final char ch;\n" +
                "            final byte b;\n" +
                "            public Temp(char ch, byte b) {\n" +
                "                this.ch = ch;\n" +
                "                this.b = b;\n" +
                "            }\n" +
                "        }\n" +
                "        Stack<Temp> s = new Stack<>();\n" +
                "        ArrayList<String> v = new ArrayList<>(Arrays.asList(\"\", \"\", \"2\", \"'\"));\n" +
                "        ArrayList<String> result = new ArrayList<>();\n" +
                "        for (int i = 0; i < moves.length(); i++) {\n" +
                "            if (s.isEmpty() || s.peek().ch != moves.charAt(i))\n" +
                "                s.push(new Temp(moves.charAt(i), (byte) 1));\n" +
                "            else {\n" +
                "                Temp x = s.pop();\n" +
                "                if (x.b != (byte) 3)\n" +
                "                    s.push(new Temp(x.ch, (byte) (x.b + 1)));\n" +
                "            }\n" +
                "        }\n" +
                "        while (!s.isEmpty()) {\n" +
                "            Temp x = s.pop();\n" +
                "            if (x.b != 0)\n" +
                "                result.add(0, x.ch + v.get(x.b));\n" +
                "        }\n" +
                "        return result;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String toString() {\n" +
                "        return \"Cube{\\n\" +\n" +
                "                \"edge=\" + edge.toString() +\n" +
                "                \",\\ncorner=\" + corner.toString() +\n" +
                "                \"\\n}\";\n" +
                "    }\n" +
                "\n" +
                "    public Edge getEdge() {\n" +
                "        return edge;\n" +
                "    }\n" +
                "\n" +
                "    public void setEdge(Edge edge) {\n" +
                "        this.edge = edge;\n" +
                "    }\n" +
                "\n" +
                "    public Corner getCorner() {\n" +
                "        return corner;\n" +
                "    }\n" +
                "\n" +
                "    public void setCorner(Corner corner) {\n" +
                "        this.corner = corner;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "// U2 L2 F R D R F R F' L' B U2 F R2 L2 F' U2 B L2 B\n";

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