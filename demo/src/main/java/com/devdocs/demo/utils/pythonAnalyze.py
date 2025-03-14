import ast
import json
import sys
from collections import defaultdict

class PythonFileStructure:
    def __init__(self):
        self.imports = []
        self.classes = []
        self.functions = []

    class PythonClassStructure:
        def __init__(self, name, extended_classes=None, annotations=None):
            self.name = name
            self.extended_classes = extended_classes or []
            self.annotations = annotations or []
            self.methods = []

    class PythonMethodStructure:
        def __init__(self, name, return_type=None, parameters=None, body=None, annotations=None):
            self.name = name
            self.return_type = return_type or ""
            self.parameters = parameters or []
            self.body = body or ""
            self.annotations = annotations or []

class CodeAnalyzer(ast.NodeVisitor):
    def __init__(self):
        self.file_structure = PythonFileStructure()
        self.function_calls = defaultdict(set)
        self.source_code = ""

    def visit_Import(self, node):
        for alias in node.names:
            self.file_structure.imports.append(alias.name)
        self.generic_visit(node)

    def visit_ImportFrom(self, node):
        module = node.module
        for alias in node.names:
            self.file_structure.imports.append(f"{module}.{alias.name}")
        self.generic_visit(node)

    def visit_ClassDef(self, node):
        class_name = node.name
        extended_classes = [base.id for base in node.bases if isinstance(base, ast.Name)]
        annotations = [
            decorator.id for decorator in node.decorator_list if isinstance(decorator, ast.Name)
        ]
        new_class = PythonFileStructure.PythonClassStructure(class_name, extended_classes, annotations)
        self.file_structure.classes.append(new_class)
        self.generic_visit(node)

    def visit_FunctionDef(self, node):
        func_name = node.name
        args = [arg.arg for arg in node.args.args]
        return_type = ast.unparse(node.returns) if node.returns else ""
        function_body = ast.get_source_segment(self.source_code, node)
        annotations = [
            decorator.id for decorator in node.decorator_list if isinstance(decorator, ast.Name)
        ]
        new_method = PythonFileStructure.PythonMethodStructure(func_name, return_type, args, function_body, annotations)

        if self.file_structure.classes:
            self.file_structure.classes[-1].methods.append(new_method)
        else:
            self.file_structure.functions.append(new_method)

        for child in ast.walk(node):
            if isinstance(child, ast.Call) and isinstance(child.func, ast.Name):
                called_func = child.func.id
                self.function_calls[func_name].add(called_func)

        self.generic_visit(node)

    def analyze(self, source_code):
        self.source_code = source_code
        tree = ast.parse(self.source_code)
        self.visit(tree)

    def report_json(self):
        result = {
            "imports": self.file_structure.imports,
            "classes": [
                {
                    "name": cls.name,
                    "extendedClasses": cls.extended_classes,
                    "annotations": cls.annotations,
                    "methods": [
                        {
                            "name": method.name,
                            "returnType": method.return_type,
                            "parameters": method.parameters,
                            "body": method.body,
                            "annotations": method.annotations,
                        }
                        for method in cls.methods
                    ],
                }
                for cls in self.file_structure.classes
            ],
            "functions": [
                {
                    "name": func.name,
                    "returnType": func.return_type,
                    "parameters": func.parameters,
                    "body": func.body,
                    "annotations": func.annotations,
                }
                for func in self.file_structure.functions
            ],
            "function_dependencies": {func: list(called_funcs) for func, called_funcs in self.function_calls.items()}
        }
        return json.dumps(result, indent=4)

if __name__ == "__main__":
    analyzer = CodeAnalyzer()

    if len(sys.argv) > 1:
        filepath = sys.argv[1]
        with open(filepath, "r", encoding="utf-8") as f:
            source_code = f.read()
    else:
        source_code = sys.stdin.read()

    analyzer.analyze(source_code)
    print(analyzer.report_json())
