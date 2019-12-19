package test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.util.List;

import static utils.Tools.packageNameContainer;

public class testGetMethodName {

    private static void parseMethodDeclaration(CompilationUnit cu) {
        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            try {
                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
                String methodName = "";
                try {
                    methodName = resolvedMethodDeclaration.getQualifiedSignature();
                    System.out.println("methodName: " + methodName);
                } catch (UnsolvedSymbolException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String ImportPath = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util\\AbstractCollection.java";

    public static void main(String args[]) throws Exception{
        String path = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util\\AbstractCollection.java";
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
        reflectionTypeSolver.setParent(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(reflectionTypeSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        ParseResult<CompilationUnit> parseResult = javaParser.parse(new File(ImportPath));
        if (parseResult.getResult().isPresent()) {
            CompilationUnit cu = parseResult.getResult().get();
            parseMethodDeclaration(cu);
            System.out.println("\r\n");
        }
    }
}
