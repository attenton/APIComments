package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Strings;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import model.ClassModel;
import model.EntityModel;
import model.RelationModel;
import utils.GetJavaFiles;
import utils.JSONWriter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.Tools.*;

public class ClassExtractor {
    private static Set<EntityModel> entityModelSet = new HashSet<>();
    private static Set<ClassModel> classModelSet = new HashSet<>();
    private static Set<String> recordName = new HashSet<>();
    private static List<RelationModel> relationModelList = new ArrayList<>();

    private static void addEntityModelList(String qualifiedName, Integer type, String code, String comment) {
        recordName.add(qualifiedName);

        EntityModel entityModel = new EntityModel();
        qualifiedName = qualifiedName.replace(" ", "");
        entityModel.setQualified_name(qualifiedName);
        entityModel.setCode(code);
        entityModel.setType(type);
        entityModel.setComment(comment);
        entityModelSet.add(entityModel);
    }

    private static void addClassModle(String classOrInterfaceName, Integer type, String code, String class_comment, List<Comment> AllComment){
        recordName.add(classOrInterfaceName);

        ClassModel classyModel = new ClassModel();
//        classOrInterfaceName = classOrInterfaceName.replace(" ", "");
        classyModel.setQualified_name(classOrInterfaceName);
        classyModel.setCode(code);
        classyModel.setType(type);
        classyModel.setComment(class_comment);
        classModelSet.add(classyModel);
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

    private static void parseClassInterface(CompilationUnit cu, String packageName) {
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
        try {
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                String class_comment = "";
                String classOrInterfaceName = "";
                String name = classOrInterfaceDeclaration.getName().asString();
                Optional<String> classOrInterfaceNameOptional = classOrInterfaceDeclaration.getFullyQualifiedName();
                if(classOrInterfaceNameOptional.isPresent()) classOrInterfaceName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
                boolean isInterface = classOrInterfaceDeclaration.isInterface();
                Optional<Comment> commentOptional = classOrInterfaceDeclaration.getComment();
                List<Comment> commentList = classOrInterfaceDeclaration.getAllContainedComments();
                if (commentOptional.isPresent()) {
                    class_comment = commentOptional.get().getContent();
                }
                System.out.println(Strings.repeat("=", classOrInterfaceName.length()));
                if (isInterface) {
                    System.out.println("interface " + classOrInterfaceName);
                    addClassModle(classOrInterfaceName, INTERFACE_ENTITY, classOrInterfaceDeclaration.toString(), class_comment, commentList);
                } else {
                    System.out.println("Class " + classOrInterfaceName);
                    addClassModle(classOrInterfaceName, CLASS_ENTITY, classOrInterfaceDeclaration.toString(), class_comment, commentList);
                }
                if (!packageName.equals("")) addRelationModelList(classOrInterfaceName, packageName, BELONGTO);
                List<ClassOrInterfaceType> extendedTypeList = classOrInterfaceDeclaration.getExtendedTypes();
                for (ClassOrInterfaceType extendedType : extendedTypeList) {
                    try{
                        String extendName = extendedType.resolve().getQualifiedName();
                        System.out.println("extend " + extendName);
                        addRelationModelList(classOrInterfaceName, extendName, EXTEND);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
                for (ClassOrInterfaceType implementedType : implementedTypeList) {
                    try {
                        String interfaceName = implementedType.resolve().getQualifiedName();
                        addRelationModelList(classOrInterfaceName, interfaceName, IMPLEMENT);
                        System.out.println("implemented " + interfaceName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parsePackage(CompilationUnit cu) {
        String packageName = "";
        try {
            if (cu.getPackageDeclaration().isPresent()) {
                packageName = cu.getPackageDeclaration().get().getName().asString();
                addEntityModelList(packageName, PACKAGE_ENTITY, "", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }

    public static List<String> packageNameContainer = new ArrayList<>();

    public static List<String> readPackageName(){
        String fileName ="C:\\Users\\attenton\\Desktop\\PackageName.txt";
        List<String> ret= new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
//                System.out.println(line);
                ret.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    private static void cleanAll() {
        System.out.println("start clean");
        relationModelList.clear();
        entityModelSet.clear();
        recordName.clear();
        System.out.println("clean finish");
    }

    private static void startWrite() {
        System.out.println("-------start write--------");
        String temp = "C:/D/Document/Research/APIDrective/result/";
        JSONWriter.writeModelListToJson(temp + "ClassOrInterfaceAndPackageRelations.json", relationModelList);
        relationModelList.clear();
        JSONWriter.writeModelListToJson(temp + "Packages.json", entityModelSet);
        entityModelSet.clear();
        JSONWriter.writeModelListToJson(temp + "ClassOrInterfaces.json", classModelSet);
        classModelSet.clear();
        System.out.println("------finish-----------");
    }
    public static String path = "C:\\D\\Document\\Research\\APIDrective\\src";

    public static void main(String args[]) throws Exception{
        cleanAll();
        packageNameContainer = readPackageName();
        JavaParser javaParser = new JavaParser();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver(false);
        reflectionTypeSolver.setParent(reflectionTypeSolver);
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(reflectionTypeSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
        File projectDir = new File(path);
        List<String> pathList = GetJavaFiles.listClasses(projectDir);
        for(String path : pathList) {
            try {
                ParseResult<CompilationUnit> parseResult = javaParser.parse(new File(path));
                if (parseResult.getResult().isPresent()) {
                    CompilationUnit cu = parseResult.getResult().get();
                    String packageName = parsePackage(cu);
//                System.out.println(packageName);
                    if (!packageNameContainer.contains(packageName)) continue;
//                System.out.println(packageName);
                    parseClassInterface(cu, packageName);
                    System.out.println("\r\n");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        startWrite();
    }
}
