package utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocDescriptionElement;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class parseComment {
    private static void parseMethodDeclaration(CompilationUnit cu) {
        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            try {
                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
                String methodName = "";
                try {
                    methodName = resolvedMethodDeclaration.getQualifiedSignature();
                    System.out.println("======================================");
                    System.out.println("methodName: " + methodName);
                    Optional<JavadocComment> javadocCommentOptional = methodDeclaration.getJavadocComment();

                    if(javadocCommentOptional.isPresent()) {
                        JavadocComment javadocComment = javadocCommentOptional.get();
//                        System.out.println("javadocComment:\n " + javadocComment);
                        Javadoc javadoc = javadocComment.parse();
                        JavadocDescription javadocDescription = javadoc.getDescription();

                        System.out.println("javadoc: \n" + javadocComment);
                        List<JavadocDescriptionElement> javadocDescriptionElements = javadocDescription.getElements();
                        System.out.println("splitdescription: ");
                        for(JavadocDescriptionElement javadocDescriptionElement : javadocDescriptionElements){
                            System.out.println(javadocDescriptionElement.toText());
                            System.out.println("----count--");
                        }
                        System.out.println("\n");
                        System.out.println("description: \n" + javadocDescription.toText());
                        System.out.println("\n");
                        List<JavadocBlockTag> javadocBlockTags = javadoc.getBlockTags();
                        for(JavadocBlockTag javadocBlockTag : javadocBlockTags){
                            System.out.println(javadocBlockTag.toString());
                            System.out.println("TagName: " + javadocBlockTag.getTagName());
                            System.out.println("Name:" + javadocBlockTag.getName());
                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }
                    }
                    System.out.println("\n");
                } catch (UnsolvedSymbolException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String ImportPath = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util\\AbstractCollection.java";

    public static void main(String[] args) throws Exception{
        String path = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util\\AbstractCollection.java";
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
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
