package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
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
import model.EntityModel;
import model.MethodModel;
import model.RelationModel;
import utils.GetJavaFiles;
import utils.JSONWriter;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.Tools.*;
import static utils.Tools.packageNameContainer;

public class MethodExtractor {
    private static Set<MethodModel> methodModelSet = new HashSet<>();
    private static List<RelationModel> relationModelList = new ArrayList<>();
//    private static List<RelationModel> exceptionRelationModelList = new ArrayList<>();
//    private static List<RelationModel> parameterRelationModelList = new ArrayList<>();
//    private static List<RelationModel> classRelationModelList = new ArrayList<>();

    private static void parseMethodDeclaration(CompilationUnit cu) {
        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            try {
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
                String methodName = "";
                String belongClassName = "";
                String full_delcaration = "";
                try {
                    ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(methodDeclaration, 0);
                    if(parentClass != null) {
                        belongClassName = parentClass.resolve().getQualifiedName();
                        addRelationModelList(methodName, belongClassName, BELONGTO);
                        System.out.println("className: " + belongClassName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    methodName = methodDeclaration.resolve().getQualifiedSignature();
                    full_delcaration = methodDeclaration.getDeclarationAsString();
                    System.out.println("full_delcaration: " + full_delcaration);
                    System.out.println("belongClassName: " + belongClassName);
//                    methodName = (getMethodName(belongClassName, full_delcaration));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("methodName: " + methodName);
//                addRelationModelList(methodName, belongClassName, BELONGTO);
                StringBuilder declare = new StringBuilder();
                String codeString = methodDeclaration.toString();
                String commentString = "";
//                EnumSet<Modifier> modifiers = methodDeclaration.getModifiers();
                List<Modifier> modifiers = methodDeclaration.getModifiers();
//                for (Modifier m : modifiers) {
//                    System.out.println("modifiers: " + m.toString());
//                }
                Type typeReturn = methodDeclaration.getType();
                List<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
                List<Parameter> parameterList = methodDeclaration.getParameters();
                for (Parameter p : parameterList) {
                    addRelationModelList(methodName, p.toString(), EXCEPTION_THROW);
//                    System.out.println("Parameter: " + p.toString());
                }
                for (ReferenceType t : thrownExceptions) {
                    addRelationModelList(methodName, t.toString(), EXCEPTION_THROW);
//                    System.out.println("thrownExceptions： " + t.toString());
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

    private static void addRelationModelList(String startName, String endName, Integer relationType) {
        startName = startName.replace("\n", "");
        if ((!startName.equals("")) && (!endName.equals(""))) {
            RelationModel relationModel = new RelationModel();
            startName = startName.replace(", ", ",");
            endName = endName.replace(", ", ",");
            relationModel.setStart_name(startName);
            relationModel.setRelation_type(relationType);
            relationModel.setEnd_name(endName);
            relationModelList.add(relationModel);
        }
    }

    private static void addMethodEntityModelList(String full_declaration, String methodName, String code, Type typeReturn, String comment, List<String> insideComment, List<Modifier> modifiers, List<ReferenceType> thrownExceptions, List<String> stringLiteralExpr) {
//        if (recordName.contains(methodName)) {
//            return;
//        }
//        recordName.add(methodName);
        MethodModel methodModel = new MethodModel();
        methodName = methodName.replace(", ", ",");

        methodModel.setMethod_name(methodName);
        methodModel.setType(METHOD_ENTITY);
        methodModel.setString_literal_expr(stringLiteralExpr);
        methodModel.setType_return(typeReturn.toString());
        methodModel.setComment(comment);
        methodModel.setCode(code);
        methodModel.setInside_comment(insideComment);
        List<String> thrownExceptionList = new ArrayList<>();
        List<String> modifierNameList = new ArrayList<>();
        for (ReferenceType referenceType : thrownExceptions) {
            thrownExceptionList.add(referenceType.toString());
        }
        methodModel.setThrown_exceptions(thrownExceptionList);
        for (Modifier modifier : modifiers) {
            String modifierName = modifier.toString();
            modifierNameList.add(modifierName);
        }
        methodModel.setFull_declaration(full_declaration);
        methodModel.setModifier_list(modifierNameList);

        methodModelSet.add(methodModel);
    }

    public static String getMethodName(String belongClassName, String full_declaration) {
        if (belongClassName.equals("")) {
            return "";
        }
        int end = full_declaration.indexOf(")");
        int start = full_declaration.indexOf("(");
        String right = full_declaration.substring(start, end+1);
        String left = full_declaration.replace(right, "");
        String shortName = left.split(" ")[left.split(" ").length-1];
        String result = belongClassName + "." + shortName + right;
        return result;
    }

    private static ClassOrInterfaceDeclaration getAncestorNodeClassOrInterFaceDeclaration(Node methodDeclaration, Integer recursionCount){
        Optional<Node> parent = methodDeclaration.getParentNode();
        if(parent.isPresent()) {
            if (parent.get() instanceof ClassOrInterfaceDeclaration)
                return (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
            else {
                if (recursionCount > 5) return null;
                return getAncestorNodeClassOrInterFaceDeclaration(methodDeclaration.getParentNode().get(), recursionCount);
            }
        }else {
            System.out.println("No parentNode, recursionCount: " + recursionCount);
            return null;
        }
    }

    private static void methodCleanAll() {
        System.out.println("start clean");
        methodModelSet.clear();
//        classRelationModelList.clear();
//        parameterRelationModelList.clear();
//        exceptionRelationModelList.clear();
        System.out.println("clean finish");
    }

    private static void startWrite() {
        System.out.println("-------start write--------");
        String temp = "C:/D/Document/Research/APIDrective/result/";
        JSONWriter.writeModelListToJson(temp + "Method.json", methodModelSet);
        methodModelSet.clear();
        JSONWriter.writeModelListToJson(temp + "MethodeRelations.json", relationModelList);
        relationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "MethodAndClassOrInterfaceRelations.json", classRelationModelList);
//        classRelationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "MethodAndException.json", exceptionRelationModelList);
//        exceptionRelationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "MethodAndParameter.json", parameterRelationModelList);
//        parameterRelationModelList.clear();
        System.out.println("------finish-----------");
    }

    public static void main(String args[]) throws Exception{
        methodCleanAll();
        packageNameContainer = readPackageName();
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
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
