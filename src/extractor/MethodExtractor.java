package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import utils.GetJavaFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static utils.Tools.*;
import static utils.Tools.packageNameContainer;

public class MethodExtractor {
    private static void parseMethodDeclaration(CompilationUnit cu) {
        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            try {
                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
                String methodName;
                String belongClassName = "";
                try {
                    ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
                    belongClassName = parentClass.resolve().getQualifiedName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    methodName = resolvedMethodDeclaration.getQualifiedSignature();
                } catch (UnsolvedSymbolException e) {
                    methodName = (getMethodName(methodDeclaration.getTokenRange().get().toString(), belongClassName, methodDeclaration.getName().toString()));
                }

                addRelationModelList(methodName, belongClassName, BELONGTO);

                StringBuilder declare = new StringBuilder();
                String codeString = methodDeclaration.toString();

                String commentString = "";

                EnumSet<Modifier> modifiers = methodDeclaration.getModifiers();
                for (Modifier m : modifiers) {
                    declare.append(m.asString()).append(" ");
                }
                Type typeReturn = methodDeclaration.getType();
                declare.append(typeReturn.toString()).append(" ");
                declare.append(methodDeclaration.getName()).append("(");
                List<Parameter> parameterList = methodDeclaration.getParameters();
                int i = 0;
                for (Parameter p : parameterList) {
                    declare.append(p);
                    if (i != parameterList.size() - 1) {
                        declare.append(" ");
                    }
                    i++;
                }
                declare.append(")");
                List<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
                if (thrownExceptions.size() > 0) {
                    declare.append(" throw ");
                }
                for (ReferenceType t : thrownExceptions) {
                    declare.append(t.toString()).append(" ");
                }
                List<BlockComment> blockCommentList = methodDeclaration.getChildNodesByType(BlockComment.class);
                List<String> insideComment = new ArrayList<>();
                for (BlockComment blockComment : blockCommentList) {
                    insideComment.add(blockComment.toString());
                }
                List<LineComment> lineCommentList = methodDeclaration.getChildNodesByType(LineComment.class);
                for (LineComment lineComment : lineCommentList) {
                    insideComment.add(lineComment.toString());
                }
                Optional<JavadocComment> commentOptional = methodDeclaration.getJavadocComment();
                if (commentOptional.isPresent()) {
                    JavadocComment comment = commentOptional.get();
                    commentString = comment.getContent();
                }
                List<StringLiteralExpr> stringLiteralExprList = methodDeclaration.getChildNodesByType(StringLiteralExpr.class);
                List<String> literalStringList = new ArrayList<>();
                for (StringLiteralExpr stringLiteralExpr : stringLiteralExprList) {
                    String v = stringLiteralExpr.getValue();
                    if (!v.equals("") && (!v.matches("-?[0-9]+.*[0-9]*") && (!v.equals(".class") && (!v.equals("class") && (v.length() > 1))))) {
                        literalStringList.add(v);
                    }
                }
                addMethodEntityModelList(declare.toString(), methodName, codeString, typeReturn, commentString, insideComment, modifiers, thrownExceptions, literalStringList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws Exception{
        cleanAll();
        packageNameContainer = readPackageName();
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
        reflectionTypeSolver.setParent(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(reflectionTypeSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        File projectDir = new File(ImportPath);
        List<String> pathList = GetJavaFiles.listClasses(projectDir);
        for(String path : pathList) {
            try {
                ParseResult<CompilationUnit> parseResult = javaParser.parse(new File(path));
                if (parseResult.getResult().isPresent()) {
                    CompilationUnit cu = parseResult.getResult().get();
                    String packageName = cu.getPackageDeclaration().get().getName().asString();
//                System.out.println(packageName);
                    if (!packageNameContainer.contains(packageName)) continue;

                    parseMethodDeclaration(cu);
                    System.out.println("\r\n");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        startWrite();
    }
}
