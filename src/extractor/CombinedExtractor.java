package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import javafx.util.Pair;
import model.ClassModel;
import model.CompilationUnitModel;
import model.TreeNode;
import sun.reflect.generics.tree.Tree;
import utils.GetJavaFiles;


import java.io.File;
import java.util.*;

import static utils.Tools.*;
import static utils.Tools.packageNameContainer;

public class CombinedExtractor {
    private static Set<ClassModel> classModelSet = new HashSet<>();
    private static List<TreeNode<Pair<String, CompilationUnit>>> treeList = new ArrayList<>();

    private static void parseAll(CompilationUnit cu){
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
        try {
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                String classOrInterfaceName = "";
                Optional<String> classOrInterfaceNameOptional = classOrInterfaceDeclaration.getFullyQualifiedName();
                if(classOrInterfaceNameOptional.isPresent()) classOrInterfaceName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
                Pair<String, CompilationUnit> pair = new Pair<>(classOrInterfaceName, cu);
                TreeNode<Pair<String, CompilationUnit>> newTreeNode = new TreeNode<>(pair);
                List<ClassOrInterfaceType> extendedTypeList = classOrInterfaceDeclaration.getExtendedTypes();
                for (ClassOrInterfaceType extendedType : extendedTypeList) {
                    try{
                        String extendName = extendedType.resolve().getQualifiedName();
                        System.out.println("extend " + extendName);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
                for (ClassOrInterfaceType implementedType : implementedTypeList) {
                    try {
                        String interfaceName = implementedType.resolve().getQualifiedName();
                        System.out.println("implemented " + interfaceName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
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
                    parseAll(cu);
                    System.out.println("\r\n");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
