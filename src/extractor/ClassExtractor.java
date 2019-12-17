package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Strings;
import model.ClassModel;
import utils.GetJavaFiles;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClassExtractor {
    private static Set<ClassModel> classModelSet = new HashSet<>();

    private static void parseClassInterface(CompilationUnit cu, String packageName) {
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
        try {
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                String class_comment = "";
                String classOrInterfaceName = "";
//                String classOrInterfaceName = classOrInterfaceDeclaration.resolve().getQualifiedName();
                Optional<String> classOrInterfaceNameOptional = classOrInterfaceDeclaration.getFullyQualifiedName();
                if(classOrInterfaceNameOptional.isPresent()) classOrInterfaceName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
//                System.out.println(classOrInterfaceName);
                boolean isInterface = classOrInterfaceDeclaration.isInterface();
                Optional<Comment> commentOptional = classOrInterfaceDeclaration.getComment();
//                List<Comment> commentList = classOrInterfaceDeclaration.getAllContainedComments();
                if (commentOptional.isPresent()) {
                    class_comment = commentOptional.get().getContent();
                }
                System.out.println(Strings.repeat("=", classOrInterfaceName.length()));
                System.out.println("classOrInterfaceDeclaration:\n " + classOrInterfaceDeclaration.getMetaModel().toString());
                System.out.println("comment:\n " + class_comment);
                if (isInterface) {
                    System.out.println("interface " + classOrInterfaceName);
//                    nameOfInterfaceSet.add(classOrInterfaceName);
//                    addEntityModelList(classOrInterfaceName, INTERFACE_ENTITY, classOrInterfaceDeclaration.toString(), class_comment);
                } else {
                    System.out.println("Class " + classOrInterfaceName);
//                    addEntityModelList(classOrInterfaceName, CLASS_ENTITY, classOrInterfaceDeclaration.toString(), class_comment);
//                    nameOfClassSet.add(classOrInterfaceName);
                }
                List<ClassOrInterfaceType> extendedTypeList = classOrInterfaceDeclaration.getExtendedTypes();
                for (ClassOrInterfaceType extendedType : extendedTypeList) {
                    String extendName = extendedType.resolve().getQualifiedName();
                    System.out.println("extend " + extendName);
//                    if (!nameOfClassSet.contains(extendName)) {
//                        addEntityModelList(extendName, CLASS_ENTITY, classOrInterfaceDeclaration.toString(), "");
//                    }
//                    addRelationModelList(classOrInterfaceName, extendName, EXTEND);
//                    if (!packageName.equals("")) {
//                        addRelationModelList(classOrInterfaceName, packageName, BELONGTO);
//                    }
                }
                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
                for (ClassOrInterfaceType implementedType : implementedTypeList) {
                    try {
                        String interfaceName = implementedType.resolve().getQualifiedName();
                        System.out.println("implemented " + interfaceName);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
//                    addRelationModelList(classOrInterfaceName, interfaceName, IMPLEMENT);
//                    if (!nameOfInterfaceSet.contains(interfaceName)) {
//                        addEntityModelList(interfaceName, INTERFACE_ENTITY, classOrInterfaceDeclaration.toString(), "");
//                    }
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
//                addEntityModelList(packageName, PACKAGE_ENTITY, "", "");
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
//    public static String path = "C:\\D\\Document\\Research\\APIDrective\\src";
    public static String path = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util";
    public static void main(String args[]) throws Exception{
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
            ParseResult<CompilationUnit> parseResult = javaParser.parse(new File(path));
            if (parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();
                String packageName = parsePackage(cu);
//                System.out.println(packageName);
                if(!packageNameContainer.contains(packageName)) continue;
//                System.out.println(packageName);
                parseClassInterface(cu, packageName);
                System.out.println("\r\n");
            }
        }
    }
}
