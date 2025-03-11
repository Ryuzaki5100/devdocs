# GitHubRepoContents Functions Documentation

This documentation covers a suite of functions in the `GitHubRepoContents` class that interact with the GitHub API to retrieve repository contents. The functions are interconnected, and will be explored in the order they are executed.

## 1. `main(String[] args)`

### Overview
The `main` function serves as the entry point for the program execution. It sets out to either list the file structure of a GitHub repository or retrieve the contents of a specific file based on the defined constants for `OWNER`, `REPO`, and `BRANCH`.

### Functionality
- The function's primary responsibility is to initiate the operations for repository content retrieval.
- It is designed to capture any exceptions that might occur during execution to ensure that issues are logged.

### Business Logic
- The primary goal is to fetch and display the contents of a specific Java file from a GitHub repository by using the other helper functions within the class.

### Dependencies
- `getFileContents()` is called to fetch the file content of a predefined file path within the repository.

---

## 2. `getFileContents(String owner, String repo, String branch, String path)`

### Overview
This function retrieves the contents of a specified file located in the given repository branch. 

### Functionality
- Constructs a URL using the repository owner's name, repository name, branch, and the path to the file.
- Opens an HTTP connection and sets the necessary request properties, including authorization token and content acceptance headers.
- It invokes `checkRateLimit()` to ensure that the API rate limits are respected before proceeding with the request.
- Processes the HTTP response:
  - If the response code is `200 OK`, it reads and returns the decoded content of the file in a human-readable format.
  - If the response code is anything other than `200`, it delegates error handling to `handleErrorResponse()`.

### Business Logic
- This function is essential for fetching file contents to perform tasks such as parsing or displaying data from source code files hosted on GitHub.

### Dependencies
- `checkRateLimit()`, `handleErrorResponse()`.

---

## 3. `listFileStructure(String owner, String repo, String branch, String path)`

### Overview
`listFileStructure` allows users to explore the folder structure of a repository, recursively listing files and directories.

### Functionality
- It constructs an API URL to fetch the directory contents for a specified path.
- Initiates a connection and sets request headers like `Authorization` using a token, and a specified `Accept` content type.
- Calls `checkRateLimit()` to ensure compliance with API request limits.
- Processes the response:
  - If successful (response code 200), it reads the content as a JSON array, iterating through items and distinguishing between files and directories. It logs paths and recursively calls itself for directories.
  - In case of failure, it relies on `handleErrorResponse()` for error reporting.

### Business Logic
- This function is ideal for obtaining an overview of repository contents, particularly useful for navigation and development tasks that require awareness of the file structure.

### Dependencies
- `checkRateLimit()`, `handleErrorResponse()`.

---

## 4. `handleErrorResponse(HttpURLConnection connection)`

### Overview
Handles error responses from the GitHub API, providing insights into failures during API calls.

### Functionality
- Retrieves the HTTP response code using the connection object and logs an appropriate message.
- Reads error messages from the input stream and accumulates them for display.
- Closes the connection and prints the error content for debugging purposes.

### Business Logic
- This function serves as an essential tool for troubleshooting and understanding issues when requests to the GitHub API fail, ensuring that developers receive detailed feedback on API failures.

### Dependencies
- None.

---

## 5. `checkRateLimit(HttpURLConnection connection)`

### Overview
This function checks the remaining rate limit for API requests and handles situations where the limit has been reached.

### Functionality
- It inspects API response headers for the `X-RateLimit-Remaining` field to determine if any requests can be made.
- If the remaining quota is `0`, the function pauses execution by sleeping for a predefined delay (`RATE_LIMIT_DELAY`) to prevent further API attempts until the limit resets.

### Business Logic
- Rate limiting is a critical part of using the GitHub API, and this function ensures that applications do not exceed their quota, which could lead to blocked requests and interrupted service.

### Dependencies
- None.

---

## Conclusion
The `GitHubRepoContents` class offers a structured approach to interact with the GitHub API, allowing for the retrieval of repository contents and effective error management. Each function plays a vital role in maintaining seamless execution while adhering to API constraints.
## UML Diagram
![Image](images/GitHubRepoContents_img1.png)

