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
import static extractor.MethodExtractor.getMethodName;
import static utils.Tools.*;
import static utils.Tools.packageNameContainer;

public class FirstLevelExtractor {
    private static List<TempMethod> methodModelSet = new ArrayList<>();
    private static List<TempClass> classModelSet = new ArrayList<>();
    private static HashMap<String, Integer> classId = new HashMap<>();
    private static HashMap<String, Integer> methodId = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> classMethod = new HashMap<>();
    private static Integer sId = 0;
    private static Integer mId = 0;

    private static void parseMethod(CompilationUnit cu) {
        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            try {
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
                String methodName = "";
                String belongClassName = "";
                String name = "";
                String full_declaration = "";
                System.out.println("=========================");
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
                    full_declaration = methodDeclaration.getDeclarationAsString();
                    int end = full_declaration.indexOf(")");
                    int start = full_declaration.indexOf("(");
                    String right = full_declaration.substring(start, end+1);
                    name += right;
                    System.out.println("full_delcaration: " + full_declaration);
                    System.out.println("belongClassName: " + belongClassName);
                } catch (Exception e) {
                    methodName = getMethodName(belongClassName, full_declaration);
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
//                            System.out.println("TagName: " + javadocBlockTag.getTagName());
//                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }
                        if(tagName.equals("param")){
                            Pair<String, String> re = new Pair<String, String>(javadocBlockTag.getName().get(), javadocBlockTag.getContent().toText());
                            paramsTag.add(re);
//                            System.out.println("TagName: " + javadocBlockTag.getTagName());
//                            System.out.println("Name:" + javadocBlockTag.getName());
//                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }
                        if(tagName.equals("throw")){
                            Pair<String, String> re = new Pair<String, String>(javadocBlockTag.getName().get(), javadocBlockTag.getContent().toText());
                            throwsTag.add(re);
//                            System.out.println("TagName: " + javadocBlockTag.getTagName());
//                            System.out.println("Name:" + javadocBlockTag.getName());
//                            System.out.println("Content: " + javadocBlockTag.getContent().toText());
                        }

                    }
                }
                List<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
                List<Parameter> parameterList = methodDeclaration.getParameters();
                List<String> palist = new LinkedList<>();
                List<String> telist = new LinkedList<>();
                for (Parameter p : parameterList) {
                    palist.add(p.toString());
//                    System.out.println("Parameter: " + p.toString());
                }
                for (ReferenceType t : thrownExceptions) {
                    telist.add(t.asString());
//                    System.out.println("thrownExceptions： " + t.asString());
                }
                TempMethod tempMethod = new TempMethod();
                tempMethod.setBelongClass(belongClassName);
                tempMethod.setDescription(description);
                tempMethod.setMethodName(methodName);
                System.out.println("methodName: " + methodName);
                System.out.println("name: " + name);
                tempMethod.setName(name);
                tempMethod.setParamsTag(paramsTag);
                tempMethod.setReturnTag(returnTag);
                tempMethod.setThrowsTag(throwsTag);
                tempMethod.setParameter(palist);
                tempMethod.setThrowException(telist);
                methodModelSet.add(tempMethod);
//                methodModelSet.add(tempMethod);
                HashMap<String, String> method = classMethod.get(belongClassName);
                if(method == null){
                    HashMap<String, String> methodAndName = new HashMap<>();
                    methodAndName.put(name, methodName);
                    classMethod.put(belongClassName, methodAndName);
                }
                else{
                    method.put(name, methodName);
                    classMethod.put(belongClassName, method);
                }
                methodId.put(methodName, mId);
                mId++;
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
                Queue<String> inherit = new LinkedList<>();
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
                classId.put(classOrInterfaceName, sId);
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
        int count = 0;
        for(TempMethod tempMethod: methodModelSet){
            tempMethod.setDescription(getReDescription(tempMethod));
            methodModelSet.set(count, tempMethod);
            count++;
        }
        startWrite();
    }

    private static String getReDescription(TempMethod tempMethod){
        String description = tempMethod.getDescription();
        String name = tempMethod.getName();
        System.out.println("------------------");
        System.out.println("findname: " + name);
        System.out.println("finddescription: " + description);
        System.out.println("findMethod: " + tempMethod.getMethodName());
        System.out.println("------------------");
        String belongClass = tempMethod.getBelongClass();
//        Queue<String> inheritClass = new LinkedList<>();
        if(description.contains("{@inheritDoc}")){
            TempClass tempClass= classModelSet.get(classId.get(belongClass));
            Queue<String> inherits = tempClass.getInherit();
            while(!inherits.isEmpty()) {
                String inherit = inherits.poll();
                System.out.println("ineritClass: " + inherit);
                if (classMethod.containsKey(inherit)) {
                    HashMap<String, String> method = classMethod.get(inherit);
//                System.out.println(method);
                    if (method.containsKey(name)) {
                        String methodName = method.get(name);
                        if (methodName == null) continue;
                        else {
                            TempMethod tempMethod1 = methodModelSet.get(methodId.get(methodName));
                            String description1 = tempMethod1.getDescription();
                            if (description1.contains("{@inheritDoc}")) {
                                TempClass tempClass1 = classModelSet.get(classId.get(inherit));
                                Queue<String> classInherit = tempClass1.getInherit();
                                inherits.addAll(classInherit);
                            } else if (!description1.equals("")) {
                                return description1;
                            }
                        }
                    }
                }
            }
        }
        return description;
    }

    private static String getTagDescription(TempMethod tempMethod, Pair<String, String> throwTag){
            String content = throwTag.getValue();
            if (content.contains("{inheritDoc}")) {
                String name = tempMethod.getName();
                String belongClass = tempMethod.getBelongClass();
                TempClass tempClass = classModelSet.get(classId.get(belongClass));
                Queue<String> inherits = tempClass.getInherit();
                while (!inherits.isEmpty()) {
                    String inherit = inherits.poll();
                    System.out.println("ineritClass: " + inherit);
                    if (classMethod.containsKey(inherit)) {
                        HashMap<String, String> method = classMethod.get(inherit);
                        if (method.containsKey(name)) {
                            String methodName = method.get(name);
                            if (methodName == null) continue;
                            else {
                                TempMethod tempMethod1 = methodModelSet.get(methodId.get(methodName));
                                List<Pair<String, String>> paramsTags = tempMethod1.getParamsTag();
                                for (Pair<String, String> param : paramsTags) {
                                    if (param.getKey().equals(tagName)) {
                                        if (param.getValue().contains("{inheritDoc}")) {
                                            TempClass tempClass1 = classModelSet.get(classId.get(inherit));
                                            Queue<String> classInherit = tempClass1.getInherit();
                                            inherits.addAll(classInherit);
                                        } else if (!param.getValue().equals("")) {
                                            return param.getValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return description;
    }


    private static void startWrite() {
        System.out.println("-------start write--------");
        String temp = "C:/D/Document/Research/APIDrective/result/";
        JSONWriter.writeModelListToJson(temp + "MethodAll.json", methodModelSet);
        methodModelSet.clear();
        JSONWriter.writeModelListToJson(temp + "ClassAll.json", classModelSet);
        classModelSet.clear();
//        JSONWriter.writeModelListToJson(temp + "ClassId.json", classId);
//        classId.clear();
//        JSONWriter.writeModelListToJson(temp + "classMethod.json", classMethod);
//        classMethod.clear();
        System.out.println("------finish-----------");
    }
}
