package extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.base.Strings;
import model.ClassModel;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ClassExtractor {
    private static Set<ClassModel> classModelSet = new HashSet<>();

    private static void parseClassInterface(CompilationUnit cu, String packageName) {
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
        try {
            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
                String class_comment = "";
//                String classOrInterfaceName = classOrInterfaceDeclaration.resolve().getQualifiedName();
                String classOrInterfaceName = classOrInterfaceDeclaration.getFullyQualifiedName().get();
                System.out.println(classOrInterfaceName);
                boolean isInterface = classOrInterfaceDeclaration.isInterface();
                Optional<Comment> commentOptional = classOrInterfaceDeclaration.getComment();
                if (commentOptional.isPresent()) {
                    class_comment = commentOptional.toString();
                }
                System.out.println(Strings.repeat("=", classOrInterfaceName.length()));
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
                    String extendName = extendedType.getName().asString();
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
                    String interfaceName = implementedType.resolve().getQualifiedName();
                    System.out.println("implemented " + interfaceName);
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
    public static String path = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util\\ArrayList.java";
    public static void main(String args[]) throws Exception{

        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> parseResult = javaParser.parse(new File(path));
//        CompilationUnit cu = javaParser.parse(new File(path));
        if(parseResult.getResult().isPresent()){
            CompilationUnit cu = parseResult.getResult().get();
            String packageName = parsePackage(cu);
            System.out.println(packageName);
            parseClassInterface(cu, packageName);
        }
    }
}
