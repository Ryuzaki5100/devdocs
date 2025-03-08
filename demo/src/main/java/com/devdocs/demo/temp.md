# Cube Class Documentation

This document provides a developer-friendly overview of the `Cube` Java class, designed to represent a Rubik's Cube state and its manipulations.

## Overview

The `Cube` class is designed to model a Rubik's Cube. It manages the positions and orientations of edges and corners, and provides methods to execute moves and manipulate the cube's state. It implements the `Cloneable` interface, allowing for state duplication.

## Imports

The class utilizes the following Java utility and concurrency packages:

-   `java.util`: For data structures like `Map`, `List`, `ArrayList`, and `Stack`.
-   `java.util.concurrent`: For concurrent operations (though not directly used in the provided code, it's imported).
-   `java.util.concurrent.atomic.AtomicReference`: For atomic references (not directly used, but imported).

## Class: Cube

### Implemented Interfaces

-   `Cloneable`: Indicates that instances of this class can be cloned.

### Fields

The `Cube` class contains numerous fields to represent the cube's state and move logic:

-   `Map<Character, EdgePos> nextEdgePos`: Maps move characters to the resulting `EdgePos` after the move.
-   `Map<Character, CornerPos> nextCornerPos`: Maps move characters to the resulting `CornerPos` after the move.
-   `Map<Character, List<Map<Byte, Byte>>> nextEdgeOrientation`: Maps move characters to a list of maps, representing edge orientation changes.
-   `Map<Character, List<Map<Byte, Byte>>> nextCornerOrientation`: Maps move characters to a list of maps, representing corner orientation changes.
-   `byte[][] edgeList`: A 2D array representing edge positions.
-   `byte[][] cornerList`: A 2D array representing corner positions.
-   `Map<Character, Byte> binEncoding`: Maps move characters to their binary encoding.
-   `Map<Character, Byte> priority`: Maps move characters to their priority.
-   `Map<Byte, Byte> edgeNumberForPos`: Maps edge positions to their number.
-   `Map<Byte, Byte> cornerNumberForPos`: Maps corner positions to their number.
-   `int[][] edgePossiblePlacesStage3`: A 2D array representing possible edge places in stage 3.
-   `int[][] cornerPossiblePlacesStage3`: A 2D array representing possible corner places in stage 3.
-   `Edge edge`: An `Edge` object representing the cube's edges.
-   `Corner corner`: A `Corner` object representing the cube's corners.

### Methods

-   `Cube clone()`:
    -      Overrides the `clone()` method.
    -      Creates and returns a deep copy of the `Cube` instance.
    -      Uses the `Edge` and `Corner` clone methods.
    -   Example:
        ```java
        Cube originalCube = new Cube(new Edge(), new Corner()); //Assume Edge and Corner have default constructors
        Cube clonedCube = originalCube.clone();
        ```
-   `Cube execute(Cube c, String s)`:
    -      Executes a sequence of moves on a given `Cube`.
    -      `c`: The `Cube` to execute moves on.
    -   `s`: A string representing the moves to execute (e.g., "R U'").
    -   Handles move notation (e.g., "R2", "R'").
    -   Applies edge and corner position and orientation changes based on the move sequence.
    -   Returns the modified `Cube`.
    -   Example:
        ```java
        Cube myCube = new Cube(new Edge(), new Corner()); //Assume Edge and Corner have default constructors
        String moves = "R U R'";
        Cube movedCube = Cube.execute(myCube, moves);
        ```
-   `String reverseAlgorithm(String s)`:
    -      Reverses a given move sequence.
    -   `s`: The move sequence to reverse.
    -   Each move is turned into a 3 times repetition of the move, and then the whole string is reversed.
    -   Returns the reversed move sequence.
    -   Example:
        ```java
        String algorithm = "R U R'";
        String reversed = Cube.reverseAlgorithm(algorithm); // reversed will be "R' U' R'"
        ```
-   `ArrayList<String> getAlgorithm(String moves)`:
    -   Simplifies a move sequence by combining adjacent moves.
    -   `moves`: The move sequence to simplify.
    -   Uses a `Stack` to track and combine moves.
    -   Handles move notation (e.g., "R", "R'", "R2").
    -   Returns a simplified `ArrayList` of move strings.
    -   Example:
        ```java
        String complexMoves = "R R R U U' R";
        ArrayList<String> simplifiedMoves = new Cube(new Edge(), new Corner()).getAlgorithm(complexMoves);
        // simplifiedMoves will contain ["R'", "U", "R"]
        ```

## Class: Temp (Inner Class)

This class is defined inside the `getAlgorithm` method.

### Fields

-   `char ch`: The move character.
-   `byte b`: The move count (1, 2, or 3).

## Methods
These methods are related to the Cube class, but are located inside the Temp class.

-   `String toString()`: returns the string representation of the Cube class, by calling the toString method of the Edge and Corner classes.
    - Example:
        ```java
        Cube myCube = new Cube(new Edge(), new Corner());
        String cubeString = myCube.toString();
        System.out.println(cubeString);
        ```
-   `Edge getEdge()`: returns the Edge object of the cube.
    - Example:
        ```java
        Cube myCube = new Cube(new Edge(), new Corner());
        Edge edge = myCube.getEdge();
        ```
-   `void setEdge(Edge edge)`: sets the Edge object of the cube.
    - Example:
        ```java
        Cube myCube = new Cube(new Edge(), new Corner());
        Edge newEdge = new Edge();
        myCube.setEdge(newEdge);
        ```
-   `Corner getCorner()`: returns the Corner object of the cube.
    - Example:
        ```java
        Cube myCube = new Cube(new Edge(), new Corner());
        Corner corner = myCube.getCorner();
        ```
-   `void setCorner(Corner corner)`: sets the Corner object of the cube.
    - Example:
        ```java
        Cube myCube = new Cube(new Edge(), new Corner());
        Corner newCorner = new Corner();
        myCube.setCorner(newCorner);
        ```

## Usage Notes

-      The `Cube` class heavily relies on `Map` and `List` data structures for move logic.
-      The `execute` method is the core method for applying moves to the cube.
-   The `getAlgorithm` method is used for optimizing move sequences.
-   The `Edge` and `Corner` classes, not detailed here, are essential for representing the cube's state.
-   The `Temp` class is used as a helper class for the `getAlgorithm` method.