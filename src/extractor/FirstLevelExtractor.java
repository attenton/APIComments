package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Strings;
import javafx.util.Pair;
import model.*;
import utils.GetJavaFiles;
import utils.JSONWriter;

import java.io.File;
import java.util.*;

import static extractor.MethodExtractor.getAncestorNodeClassOrInterFaceDeclaration;
import static utils.Tools.*;
import static utils.Tools.packageNameContainer;

public class FirstLevelExtractor {
    private static Set<TempMethod> methodModelSet = new HashSet<>();
    private static Set<TempClass> classModelSet = new HashSet<>();
    private static Set<Pair<String, Integer>> classId = new HashSet<>();
    private static Set<Pair<String, String>> classMethod = new HashSet<>();
    private static Integer sId = 0;

    private static void parseMethod(CompilationUnit cu) {
        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            try {
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
                String methodName = "";
                String belongClassName = "";
                String name = "";
                String full_delcaration = "";
                name = methodDeclaration.getName().asString();
                try {
                    ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) getAncestorNodeClassOrInterFaceDeclaration(methodDeclaration, 0);
                    if(parentClass != null) {
                        belongClassName = parentClass.resolve().getQualifiedName();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<Pair<String, String>> paramsTag = new ArrayList<>();
                List<Pair<String, String>> throwsTag = new ArrayList<>();
                List<String> returnTag = new ArrayList<>();
                String description = "";
                Optional<Javadoc> javadocOptional = methodDeclaration.getJavadoc();
                if(javadocOptional.isPresent()){
                    Javadoc javadoc = javadocOptional.get();
                    description = javadoc.getDescription().toText();
                    List<JavadocBlockTag> javadocBlockTags = javadoc.getBlockTags();
                    for(JavadocBlockTag javadocBlockTag : javadocBlockTags){
                        String tagName = javadocBlockTag.getTagName();
                        if(tagName.equals("return")){
                            String re = javadocBlockTag.getContent().toText();
                            returnTag.add(re);
                            System.out.println("TagName: " + javadocBlockTag.getTagName());
                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }
                        if(tagName.equals("param")){
                            Pair<String, String> re = new Pair<String, String>(javadocBlockTag.getName().get(), javadocBlockTag.getContent().toText());
                            paramsTag.add(re);
                            System.out.println("TagName: " + javadocBlockTag.getTagName());
                            System.out.println("Name:" + javadocBlockTag.getName());
                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }
                        if(tagName.equals("param")){
                            Pair<String, String> re = new Pair<String, String>(javadocBlockTag.getName().get(), javadocBlockTag.getContent().toText());
                            throwsTag.add(re);
                            System.out.println("TagName: " + javadocBlockTag.getTagName());
                            System.out.println("Name:" + javadocBlockTag.getName());
                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }

                    }
                }
                TempMethod tempMethod = new TempMethod();
                tempMethod.setBelongClass(belongClassName);
                tempMethod.setDescription(description);
                tempMethod.setMethodName(methodName);
                tempMethod.setName(name);
                tempMethod.setParamsTag(paramsTag);
                tempMethod.setReturnTag(returnTag);
                tempMethod.setThrowsTag(throwsTag);
                methodModelSet.add(tempMethod);
                Pair<String, String> relation = new Pair<String, String>(belongClassName, name);
                classMethod.add(relation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void parseClass(CompilationUnit cu) {
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
        try {
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                String classOrInterfaceName = "";
                String description = "";
//                String name = classOrInterfaceDeclaration.getName().asString();
                List<String> inherit = new ArrayList<>();
                Optional<String> classOrInterfaceNameOptional = classOrInterfaceDeclaration.getFullyQualifiedName();
                if(classOrInterfaceNameOptional.isPresent()) classOrInterfaceName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
                boolean type = classOrInterfaceDeclaration.isInterface();
                System.out.println(Strings.repeat("=", classOrInterfaceName.length()));
                List<ClassOrInterfaceType> extendedTypeList = classOrInterfaceDeclaration.getExtendedTypes();
                for (ClassOrInterfaceType extendedType : extendedTypeList) {
                    try{
                        String extendName = extendedType.resolve().getQualifiedName();
                        inherit.add(extendName);
                        System.out.println("extend " + extendName);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
                for (ClassOrInterfaceType implementedType : implementedTypeList) {
                    try {
                        String implementedName = implementedType.resolve().getQualifiedName();
                        inherit.add(implementedName);
                        System.out.println("implemented " + implementedName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Optional<Javadoc> javadocOptional = classOrInterfaceDeclaration.getJavadoc();
                if(javadocOptional.isPresent()){
                    Javadoc javadoc = javadocOptional.get();
                    description = javadoc.getDescription().toText();
                }
                TempClass tempClass = new TempClass();
                tempClass.setDescription(description);
                tempClass.setInherit(inherit);
                tempClass.setType(type);
                tempClass.setName(classOrInterfaceName);
                classModelSet.add(tempClass);
                Pair<String, Integer> a = new Pair<>(classOrInterfaceName, sId);
                classId.add(a);
                sId++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception{
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
                    parseClass(cu);
                    parseMethod(cu);
                    System.out.println("\r\n");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        startWrite();
    }

    private static void startWrite() {
        System.out.println("-------start write--------");
        String temp = "C:/D/Document/Research/APIDrective/result/";
        JSONWriter.writeModelListToJson(temp + "MethodAll.json", methodModelSet);
        methodModelSet.clear();
        JSONWriter.writeModelListToJson(temp + "ClassAll.json", classModelSet);
        classModelSet.clear();
        JSONWriter.writeModelListToJson(temp + "ClassId.json", classId);
        classId.clear();
        JSONWriter.writeModelListToJson(temp + "classMethod.json", classMethod);
        classMethod.clear();
        System.out.println("------finish-----------");
    }
}
