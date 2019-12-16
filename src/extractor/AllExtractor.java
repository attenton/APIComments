//package extractor;
//
//
//import com.github.javaparser.JavaParser;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.Modifier;
//import com.github.javaparser.ast.body.*;
//import com.github.javaparser.ast.comments.BlockComment;
//import com.github.javaparser.ast.comments.Comment;
//import com.github.javaparser.ast.comments.JavadocComment;
//import com.github.javaparser.ast.comments.LineComment;
//import com.github.javaparser.ast.expr.*;
//import com.github.javaparser.ast.type.*;
//import com.github.javaparser.resolution.UnsolvedSymbolException;
//import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
//import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
//import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
//import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
//import com.github.javaparser.resolution.types.ResolvedArrayType;
//import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
//import com.github.javaparser.resolution.types.ResolvedType;
//import com.github.javaparser.symbolsolver.JavaSymbolSolver;
//import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
//import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
//import model.*;
//import utils.GetJavaFiles;
//import utils.JSONWriter;
//import utils.Tools;
//
//import java.io.File;
//import java.util.*;
//
//import static utils.Tools.*;
//
//public class NewExtractor {
//    private static Set<EntityModel> entityModelSet = new HashSet<>();
//    private static Set<String> parameterSet = new HashSet<>();
//    private static Set<String> nameOfClassSet = new HashSet<>();
//    private static Set<String> nameOfInterfaceSet = new HashSet<>();
//    private static Set<String> recordName = new HashSet<>();
//    private static List<ParameterModel> parameterModelList = new ArrayList<>();
//    private static List<FieldModel> fieldModelArrayList = new ArrayList<>();
//    private static List<VariableModel> variableModelsOfMethod = new ArrayList<>();
//    private static List<MethodVarModel> methodVarModelArrayList = new ArrayList<>();
//    private static List<RelationModel> relationModelList = new ArrayList<>();
//    private static List<MethodFieldRelationModel> relationModelArrayList = new ArrayList<>();
//    private static List<FieldRelationModel> fieldRelationModelList = new ArrayList<>();
//    private static List<CodeExceptionModel> exceptionModelList = new ArrayList<>();
//    private static List<TypeReturnModel> typeReturnModelList = new ArrayList<>();
//    private static List<ParameterRelationModel> parameterRelationModelList = new ArrayList<>();
//    private static List<TypeReturnRelation> typeReturnRelationList = new ArrayList<>();
//    private static List<CodeExceptionRelationModel> codeExceptionRelationModelList = new ArrayList<>();
//    private static List<String> blacklist = new ArrayList<>();
//    private static int exceptionId = 1;
//    private static int fieldId = 1;
//    private static int parameterId = 1;
//    private static int valueReturnId = 1;
//
//    private static void addEntityModelList(String qualifiedName, Integer type, String code, String comment) {
//        recordName.add(qualifiedName);
//
//        EntityModel entityModel = new EntityModel();
//        qualifiedName = qualifiedName.replace(" ", "");
//        entityModel.setQualified_name(qualifiedName);
//        entityModel.setCode(code);
//        entityModel.setType(type);
//        entityModel.setComment(comment);
//        entityModelSet.add(entityModel);
//    }
//
//    private static void addInitEntity(String qualifiedName, Integer type, String code, String comment, List<String> insideComment) {
//        if (recordName.contains(qualifiedName)) {
//            return;
//        }
//        recordName.add(qualifiedName);
//        qualifiedName = qualifiedName.replace(" ", "");
//        EntityModel entityModel = new EntityModel();
//        entityModel.setQualified_name(qualifiedName);
//        entityModel.setComment(comment);
//        entityModel.setCode(code);
//        entityModel.setInside_comment(insideComment);
//        entityModel.setType(type);
//        entityModelSet.add(entityModel);
//    }
//
//    private static String parsePackage(CompilationUnit cu) {
//        String packageName = "";
//        try {
//            if (cu.getPackageDeclaration().isPresent()) {
//                packageName = cu.getPackageDeclaration().get().getName().asString();
//                addEntityModelList(packageName, PACKAGE_ENTITY, "", "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return packageName;
//    }
//
//    private static void parseClassInterface(CompilationUnit cu, String packageName) {
//        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
//        try {
//            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
//                String class_comment = "";
//                String classOrInterfaceName = classOrInterfaceDeclaration.resolve().getQualifiedName();
//                boolean isInterface = classOrInterfaceDeclaration.isInterface();
//                Optional<Comment> commentOptional = classOrInterfaceDeclaration.getComment();
//                if (commentOptional.isPresent()) {
//                    class_comment = commentOptional.toString();
//                }
//
//                if (isInterface) {
//                    nameOfInterfaceSet.add(classOrInterfaceName);
//                    addEntityModelList(classOrInterfaceName, INTERFACE_ENTITY, classOrInterfaceDeclaration.toString(), class_comment);
//                } else {
//                    addEntityModelList(classOrInterfaceName, CLASS_ENTITY, classOrInterfaceDeclaration.toString(), class_comment);
//                    nameOfClassSet.add(classOrInterfaceName);
//                }
//                List<ClassOrInterfaceType> extendedTypeList = classOrInterfaceDeclaration.getExtendedTypes();
//                for (ClassOrInterfaceType extendedType : extendedTypeList) {
//                    String extendName = extendedType.resolve().getQualifiedName();
//                    if (!nameOfClassSet.contains(extendName)) {
//                        addEntityModelList(extendName, CLASS_ENTITY, classOrInterfaceDeclaration.toString(), "");
//                    }
//                    addRelationModelList(classOrInterfaceName, extendName, EXTEND);
//                    if (!packageName.equals("")) {
//                        addRelationModelList(classOrInterfaceName, packageName, BELONGTO);
//                    }
//                }
//                List<ClassOrInterfaceType> implementedTypeList = classOrInterfaceDeclaration.getImplementedTypes();
//                for (ClassOrInterfaceType implementedType : implementedTypeList) {
//                    String interfaceName = implementedType.resolve().getQualifiedName();
//                    addRelationModelList(classOrInterfaceName, interfaceName, IMPLEMENT);
//                    if (!nameOfInterfaceSet.contains(interfaceName)) {
//                        addEntityModelList(interfaceName, INTERFACE_ENTITY, classOrInterfaceDeclaration.toString(), "");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void parseConstructor(CompilationUnit cu) {
//        try {
//            List<ConstructorDeclaration> constructorDeclarationList = cu.findAll(ConstructorDeclaration.class);
//            for (ConstructorDeclaration constructorDeclaration : constructorDeclarationList) {
//                ResolvedConstructorDeclaration resolvedConstructorDeclaration = constructorDeclaration.resolve();
//                String classSimpleName = resolvedConstructorDeclaration.getClassName();
//                String methodName = resolvedConstructorDeclaration.getQualifiedSignature();
//
//                String methodSimpleName = resolvedConstructorDeclaration.getSignature();
//                methodName = removeAnonymous(methodName);
//                String belongClassName = Tools.getClassName(methodName, classSimpleName, methodSimpleName);
//                String constructionName = Tools.handlerInit(resolvedConstructorDeclaration.getQualifiedSignature(), resolvedConstructorDeclaration.getQualifiedName());
//                addRelationModelList(constructionName, belongClassName, BELONGTO);
//                Optional<JavadocComment> javadocComment = constructorDeclaration.getJavadocComment();
//                String comment = "";
//                String code = "";
//                if (javadocComment.isPresent()) {
//                    JavadocComment javadocComment1 = javadocComment.get();
//                    comment = javadocComment1.getContent();
//                }
//                String[] splitResult = constructorDeclaration.toString().split("public");
//
//                if (splitResult.length > 0) {
//                    code += "public";
//                    code += splitResult[splitResult.length - 1];
//                }
//                List<String> insideComment = new ArrayList<>();
//                List<LineComment> lineCommentList = constructorDeclaration.getChildNodesByType(LineComment.class);
//                for (LineComment lineComment : lineCommentList) {
//                    insideComment.add(lineComment.toString());
//                }
//
//                List<BlockComment> blockCommentList = constructorDeclaration.getChildNodesByType(BlockComment.class);
//                for (BlockComment blockComment : blockCommentList) {
//                    insideComment.add(blockComment.toString());
//                }
//
//                addInitEntity(constructionName, CONSTRUCT_ENTITY, code, comment, insideComment);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void addMethodEntityModelList(String declare, String qualifiedName, String code, Type typeReturn, String comment, List<String> insideComment, EnumSet<Modifier> modifiers, List<ReferenceType> thrownExceptions, List<String> stringLiteralExpr) {
//        if (recordName.contains(qualifiedName)) {
//            return;
//        }
//        recordName.add(qualifiedName);
//        EntityModel entityModel = new EntityModel();
//        qualifiedName = qualifiedName.replace(", ", ",");
//
//        entityModel.setQualified_name(qualifiedName);
//        entityModel.setType(METHOD_ENTITY);
//        entityModel.setString_literal_expr(stringLiteralExpr);
//        entityModel.setType_return(typeReturn.toString());
//        entityModel.setComment(comment);
//        entityModel.setCode(code);
//        entityModel.setInside_comment(insideComment);
//        List<String> thrownExceptionList = new ArrayList<>();
//        List<String> modifierNameList = new ArrayList<>();
//        for (ReferenceType referenceType : thrownExceptions) {
//            thrownExceptionList.add(referenceType.toString());
//        }
//        entityModel.setThrown_exceptions(thrownExceptionList);
//        for (Modifier modifier : modifiers) {
//            String modifierName = modifier.asString();
//            modifierNameList.add(modifierName);
//        }
//        entityModel.setDeclare(declare);
//        entityModel.setModifier_list(modifierNameList);
//
//        entityModelSet.add(entityModel);
//    }
//
//    private static void parseMethodDeclaration(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            try {
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
//                String methodName;
//                String belongClassName = "";
//                try {
//                    ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
//                    belongClassName = parentClass.resolve().getQualifiedName();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    methodName = resolvedMethodDeclaration.getQualifiedSignature();
//                } catch (UnsolvedSymbolException e) {
//                    methodName = (getMethodName(methodDeclaration.getTokenRange().get().toString(), belongClassName, methodDeclaration.getName().toString()));
//                }
//
//                addRelationModelList(methodName, belongClassName, BELONGTO);
//
//                StringBuilder declare = new StringBuilder();
//                String codeString = methodDeclaration.toString();
//
//                String commentString = "";
//
//                EnumSet<Modifier> modifiers = methodDeclaration.getModifiers();
//                for (Modifier m : modifiers) {
//                    declare.append(m.asString()).append(" ");
//                }
//                Type typeReturn = methodDeclaration.getType();
//                declare.append(typeReturn.toString()).append(" ");
//                declare.append(methodDeclaration.getName()).append("(");
//                List<Parameter> parameterList = methodDeclaration.getParameters();
//                int i = 0;
//                for (Parameter p : parameterList) {
//                    declare.append(p);
//                    if (i != parameterList.size() - 1) {
//                        declare.append(" ");
//                    }
//                    i++;
//                }
//                declare.append(")");
//                List<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
//                if (thrownExceptions.size() > 0) {
//                    declare.append(" throw ");
//                }
//                for (ReferenceType t : thrownExceptions) {
//                    declare.append(t.toString()).append(" ");
//                }
//                List<BlockComment> blockCommentList = methodDeclaration.getChildNodesByType(BlockComment.class);
//                List<String> insideComment = new ArrayList<>();
//                for (BlockComment blockComment : blockCommentList) {
//                    insideComment.add(blockComment.toString());
//                }
//                List<LineComment> lineCommentList = methodDeclaration.getChildNodesByType(LineComment.class);
//                for (LineComment lineComment : lineCommentList) {
//                    insideComment.add(lineComment.toString());
//                }
//                Optional<JavadocComment> commentOptional = methodDeclaration.getJavadocComment();
//                if (commentOptional.isPresent()) {
//                    JavadocComment comment = commentOptional.get();
//                    commentString = comment.getContent();
//                }
//                List<StringLiteralExpr> stringLiteralExprList = methodDeclaration.getChildNodesByType(StringLiteralExpr.class);
//                List<String> literalStringList = new ArrayList<>();
//                for (StringLiteralExpr stringLiteralExpr : stringLiteralExprList) {
//                    String v = stringLiteralExpr.getValue();
//                    if (!v.equals("") && (!v.matches("-?[0-9]+.*[0-9]*") && (!v.equals(".class") && (!v.equals("class") && (v.length() > 1))))) {
//                        literalStringList.add(v);
//                    }
//                }
//                addMethodEntityModelList(declare.toString(), methodName, codeString, typeReturn, commentString, insideComment, modifiers, thrownExceptions, literalStringList);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void addRelationModelList(String startName, String endName, Integer relationType) {
//        startName = startName.replace("\n", "");
//        if ((!startName.equals("")) && (!endName.equals(""))) {
//            RelationModel relationModel = new RelationModel();
//            startName = startName.replace(", ", ",");
//            endName = endName.replace(", ", ",");
//            relationModel.setStart_name(startName);
//            relationModel.setRelation_type(relationType);
//            relationModel.setEnd_name(endName);
//            relationModelList.add(relationModel);
//        }
//    }
//
//    private static void addMethodFieldRelationModelList(String startName, String endName, String typeName, Integer relationType) {
//        if ((!startName.equals("")) && (!endName.equals("")) && (!typeName.equals(""))) {
//            MethodFieldRelationModel methodFieldRelationModel = new MethodFieldRelationModel();
//            startName = startName.replace(", ", ",");
//            methodFieldRelationModel.setStart_name(startName);
//            methodFieldRelationModel.setRelation_type(relationType);
//            methodFieldRelationModel.setType_name(typeName);
//            methodFieldRelationModel.setEnd_name(endName);
//            relationModelArrayList.add(methodFieldRelationModel);
//        }
//    }
//
//    private static void parseMethodCallRelationWithQualifiedName(CompilationUnit compilationUnit) {
//        List<MethodDeclaration> methodDeclarationList = compilationUnit.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            try {
//                List<MethodCallExpr> methodCallExprList = methodDeclaration.getChildNodesByType(MethodCallExpr.class);
//                List<Parameter> parameterList = methodDeclaration.getParameters();
//                Map<String, String> parameterNameTypeMap = new HashMap<>();
//                List<VariableDeclarator> variableDeclaratorList = methodDeclaration.getChildNodesByType(VariableDeclarator.class);
//                for (VariableDeclarator variableDeclarator : variableDeclaratorList) {
//                    try {
//                        String valTypeName = getVariableTypeName(variableDeclarator);
//                        ResolvedValueDeclaration d = variableDeclarator.resolve();
//                        String valName = d.getName();
//                        parameterNameTypeMap.put(valName, valTypeName);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//                for (Parameter parameter : parameterList) {
//                    String typeName = getParameterType(parameter);
//                    String parameterName = parameter.getName().toString();
//                    parameterNameTypeMap.put(parameterName, typeName);
//                }
//                for (MethodCallExpr methodCallExpr : methodCallExprList) {
//                    try {
//                        StringBuilder callExprQualifiedName = new StringBuilder(methodCallExpr.resolve().getQualifiedName());
//                        List<Expression> arguments = methodCallExpr.getArguments();
//                        callExprQualifiedName.append("(");
//                        int i = 0;
//                        for (Expression a : arguments) {
//                            String qn = a.toString();
//
//                            if (parameterNameTypeMap.containsKey(a.toString())) {
//                                qn = parameterNameTypeMap.get(a.toString());
//                            } else {
//                                if (a.toString().contains("false") || a.toString().contains("true")) {
//                                    qn = "boolean";
//                                } else if (a instanceof IntegerLiteralExpr) {
//                                    qn = "int";
//                                } else if (a instanceof CastExpr) {
//                                    qn = ((ClassOrInterfaceType) ((((CastExpr) a).getType().asReferenceType()))).resolve().getQualifiedName();
//
//                                } else if (a instanceof BinaryExpr || a instanceof StringLiteralExpr) {
//                                    qn = "string";
//
//                                } else if (a instanceof MethodCallExpr) {
//                                    if (((((MethodCallExpr) a).resolve()).getReturnType()).asReferenceType() instanceof ReferenceTypeImpl) {
//                                        ReferenceTypeImpl referenceType = (ReferenceTypeImpl) ((((MethodCallExpr) a).resolve()).getReturnType()).asReferenceType();
//                                        qn = referenceType.getQualifiedName();
//                                    }
//                                } else if (a instanceof FieldAccessExpr) {
//                                    if (((((FieldAccessExpr) a).resolve()).getType()) instanceof ResolvedPrimitiveType) {
//                                        qn = ((((FieldAccessExpr) a).resolve()).getType()).asPrimitive().getBoxTypeQName();
//                                    }
//                                }
//                            }
//                            callExprQualifiedName.append(qn);
//                            if (i != arguments.size() - 1) {
//                                callExprQualifiedName.append(",");
//                            }
//                            i++;
//                        }
//                        callExprQualifiedName.append(")");
//                        int deep = 3;
//                        MethodDeclaration m = loopGetParentMethodDeclare(methodCallExpr, deep);
//                        if (m != null) {
//                            ResolvedMethodDeclaration resolvedMethodDeclaration = m.resolve();
//                            String methodName = resolvedMethodDeclaration.getQualifiedSignature();
//                            methodName = removeAnonymous(methodName);
//                            addRelationModelList(methodName, callExprQualifiedName.toString(), Method_Call);
//                        }
//                    } catch (Exception e) {
//                        System.out.println(e);
//                    }
//
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        }
//
//
//    }
//
////    private static void parseMethodCallRelation(CompilationUnit cu) {
////
////        List<MethodCallExpr> methodCallExprList = cu.findAll(MethodCallExpr.class);
////        for (MethodCallExpr methodCallExpr : methodCallExprList) {
////            try {
////                StringBuilder callExprQualifiedName = new StringBuilder(methodCallExpr.resolve().getQualifiedName());
////                List<Expression> arguments = methodCallExpr.getArguments();
////                callExprQualifiedName.append("(");
////                int i = 0;
////                for (Expression a : arguments) {
////                    callExprQualifiedName.append(a.toString());
////                    if (i != arguments.size() - 1) {
////                        callExprQualifiedName.append(",");
////                    }
////                    i++;
////                }
////                callExprQualifiedName.append(")");
////                int deep = 3;
////                MethodDeclaration m = loopGetParentMethodDeclare(methodCallExpr, deep);
////                if (m != null) {
////                    ResolvedMethodDeclaration resolvedMethodDeclaration = m.resolve();
////                    String methodName = resolvedMethodDeclaration.getQualifiedSignature();
////                    methodName = removeAnonymous(methodName);
////                    addRelationModelList(methodName, callExprQualifiedName.toString(), Method_Call);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////
////
////    }
//
//    private static String getParameterType(Parameter p) {
//        String typeName = "";
//        try {
//            ResolvedParameterDeclaration d = p.resolve();
//            try {
//                typeName = d.getType().asReferenceType().getQualifiedName();
//            } catch (Exception e) {
//                try {
//                    typeName = ((ResolvedPrimitiveType) d.getType()).name().toLowerCase();
//                } catch (Exception e2) {
//                    typeName = p.getTypeAsString();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return typeName;
//    }
//
//    private static void addParameterRelations(String methodName, int parameterId) {
//        if (!methodName.equals("")) {
//            ParameterRelationModel parameterRelationModel = new ParameterRelationModel(methodName, parameterId);
//            parameterRelationModelList.add(parameterRelationModel);
//        }
//    }
//
//    private static void parseMethodParameter(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            try {
//                List<Parameter> parameterList = methodDeclaration.getParameters();
//
//                String commentString = "";
//                Optional<JavadocComment> commentOptional = methodDeclaration.getJavadocComment();
//                if (commentOptional.isPresent()) {
//                    JavadocComment comment = commentOptional.get();
//                    commentString = comment.getContent();
//                }
//                Map commentInfo = getParmeterCommentInfo(commentString);
//
//                for (Parameter p : parameterList) {
//                    try {
//                        String typeName = getParameterType(p);
//                        String parameterName = p.getName().toString();
//
//                        ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
//                        String methodName = resolvedMethodDeclaration.getQualifiedSignature();
//                        methodName = removeAnonymous(methodName);
//                        String description = "";
//                        if (commentInfo.containsKey(parameterName)) {
//                            description = (String) commentInfo.get(parameterName);
//                        }
//                        ParameterModel parameterModel = new ParameterModel(typeName, parameterName, parameterId, description);
//                        parameterModelList.add(parameterModel);
//                        addParameterRelations(methodName, parameterId);
//                        parameterId++;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    private static void startWrite(String dirPathName) {
//        String dirName = path2Name.get(dirPathName);
//        System.out.println("start write");
//        String temp = "result/" + dirName + "/";
//        JSONWriter.writeModelListToJson(temp + "method_field_relation.json", relationModelArrayList);
//        relationModelArrayList.clear();
//        JSONWriter.writeModelListToJson(temp + "relations.json", relationModelList);
//        relationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "entities.json", entityModelSet);
//        entityModelSet.clear();
//        JSONWriter.writeModelListToJson(temp + "parameter.json", parameterModelList);
//        parameterModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "method_variable.json", variableModelsOfMethod);
//        variableModelsOfMethod.clear();
//        JSONWriter.writeModelListToJson(temp + "field_entities.json", fieldModelArrayList);
//        fieldModelArrayList.clear();
//        JSONWriter.writeModelListToJson(temp + "field_relation.json", fieldRelationModelList);
//        fieldRelationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "method_var_list.json", methodVarModelArrayList);
//        methodVarModelArrayList.clear();
//        JSONWriter.writeModelListToJson(temp + "parameter_relation.json", parameterRelationModelList);
//        parameterRelationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "exceptions.json", exceptionModelList);
//        exceptionModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "exception_relation.json", codeExceptionRelationModelList);
//        codeExceptionRelationModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "return_types.json", typeReturnModelList);
//        typeReturnModelList.clear();
//        JSONWriter.writeModelListToJson(temp + "return_type_relation.json", typeReturnRelationList);
//        typeReturnRelationList.clear();
//        System.out.println("finish");
//    }
//
//    private static void cleanAll() {
//        System.out.println("start clean");
//        relationModelArrayList.clear();
//        relationModelList.clear();
//        entityModelSet.clear();
//        parameterModelList.clear();
//        variableModelsOfMethod.clear();
//        fieldModelArrayList.clear();
//        fieldRelationModelList.clear();
//        methodVarModelArrayList.clear();
//        parameterRelationModelList.clear();
//        exceptionModelList.clear();
//        codeExceptionRelationModelList.clear();
//        typeReturnModelList.clear();
//        typeReturnRelationList.clear();
//        nameOfClassSet.clear();
//        nameOfInterfaceSet.clear();
//        recordName.clear();
//        parameterSet.clear();
//        exceptionId = 1;
//        fieldId = 1;
//        parameterId = 1;
//        valueReturnId = 1;
//        System.out.println("clean finish");
//    }
//
//
//    private static String getVariableTypeName(VariableDeclarator v) {
//        ResolvedValueDeclaration d = v.resolve();
//
//        String valTypeName = "";
//        try {
//            valTypeName = d.getType().asReferenceType().getQualifiedName();
//        } catch (Exception e1) {
//            try {
//                valTypeName = ((ResolvedPrimitiveType) d.getType()).name().toLowerCase();
//            } catch (Exception e2) {
//                valTypeName = v.getTypeAsString();
//            }
//        }
//        return valTypeName;
//    }
//
//    private static void addHasFieldRelation(String belongClassInterfaceName, Integer fieldId) {
//        if (!belongClassInterfaceName.equals("")) {
//            FieldRelationModel fieldRelationModel = new FieldRelationModel(belongClassInterfaceName, fieldId);
//            fieldRelationModelList.add(fieldRelationModel);
//        }
//
//    }
//
//    private static void parseField(CompilationUnit cu) {
//        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = cu.findAll(ClassOrInterfaceDeclaration.class);
//        try {
//            for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
//                try {
//                    String classOrInterfaceName = classOrInterfaceDeclaration.resolve().getQualifiedName();
//                    List<FieldDeclaration> fieldDeclarationList = classOrInterfaceDeclaration.getFields();
//                    for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
//                        List<VariableDeclarator> variables = fieldDeclaration.getVariables();
//                        for (VariableDeclarator v : variables) {
//                            ResolvedValueDeclaration d = v.resolve();
//                            String valTypeName = getVariableTypeName(v);
//                            FieldModel fieldModel = new FieldModel();
//                            fieldModel.setId(fieldId);
//                            fieldModel.setField_type(valTypeName);
//                            fieldModel.setField_name(d.getName());
//                            addHasFieldRelation(classOrInterfaceName, fieldId);
//                            fieldModelArrayList.add(fieldModel);
//                            fieldId++;
//
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    private static void addCodeExceptionRelation(String methodQualifiedName, Integer codeExceptionId) {
//        if (!(methodQualifiedName.equals(""))) {
//            CodeExceptionRelationModel codeExceptionRelationModel = new CodeExceptionRelationModel(methodQualifiedName, codeExceptionId);
//            codeExceptionRelationModelList.add(codeExceptionRelationModel);
//        }
//    }
//
//    private static void addTypeReturnRelation(String methodQualifiedName, Integer typeReturnId) {
//        if (!(methodQualifiedName.equals(""))) {
//            TypeReturnRelation typeReturnRelation = new TypeReturnRelation(methodQualifiedName, typeReturnId);
//            typeReturnRelationList.add(typeReturnRelation);
//        }
//    }
//
//    private static String parseTypeName(Type typeReturn) {
//        String typeName = "";
//        try {
//            ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) typeReturn;
//            typeName = classOrInterfaceType.resolve().getQualifiedName();
//        } catch (Exception e1) {
//            try {
//
//                VoidType voidType = (VoidType) typeReturn;
//                typeName = voidType.toString();
//
//            } catch (Exception e2) {
//                try {
//
//                    PrimitiveType primitiveType = (PrimitiveType) typeReturn;
//                    typeName = primitiveType.toString();
//                } catch (Exception e3) {
//                    try {
//
//                        ArrayType arrayType = (ArrayType) typeReturn;
//                        typeName = parseTypeName(arrayType.getComponentType());
//                        if (!typeName.equals("")) {
//                            typeName += "[]";
//                        }
//
//                    } catch (Exception e4) {
//                        e4.printStackTrace();
//                    }
//                }
//            }
//        }
//        return typeName;
//    }
//
//    private static void parseReturnValue(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            try {
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
//                String methodName = resolvedMethodDeclaration.getQualifiedSignature();
//                methodName = removeAnonymous(methodName);
//                String commentString = "";
//                Optional<JavadocComment> commentOptional = methodDeclaration.getJavadocComment();
//                if (commentOptional.isPresent()) {
//                    JavadocComment comment = commentOptional.get();
//                    commentString = comment.getContent();
//                }
//                Type typeReturn = methodDeclaration.getType();
//                String typeName = parseTypeName(typeReturn);
//
//                String description = getTypeReturnInfo(commentString);
//                TypeReturnModel typeReturnModel = new TypeReturnModel(valueReturnId, typeName, description);
//                if (!typeName.equals("")) {
//                    typeReturnModelList.add(typeReturnModel);
//                    addTypeReturnRelation(methodName, valueReturnId);
//                    valueReturnId++;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void parseExceptionInfo(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            try {
//                List<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
//                String methodName = resolvedMethodDeclaration.getQualifiedSignature();
//                methodName = removeAnonymous(methodName);
//                String commentString = "";
//                Optional<JavadocComment> commentOptional = methodDeclaration.getJavadocComment();
//                if (commentOptional.isPresent()) {
//                    JavadocComment comment = commentOptional.get();
//                    commentString = comment.getContent();
//                }
//                Map commentInfo = getThrowCommentInfo(commentString);
//
//                for (ReferenceType referenceType : thrownExceptions) {
//                    try {
//                        CodeExceptionModel codeExceptionModel = new CodeExceptionModel();
//                        codeExceptionModel.setId(exceptionId);
//                        String typeThrow = "";
//                        try {
//                            typeThrow = ((ReferenceTypeImpl) referenceType.resolve()).getQualifiedName();
//
//                        } catch (UnsolvedSymbolException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if (!typeThrow.equals("")) {
//                            codeExceptionModel.setException_type(typeThrow);
//                        } else {
//                            codeExceptionModel.setException_type(referenceType.toString());
//                        }
//                        String description = "";
//                        if ((!typeThrow.equals("")) && (commentInfo.containsKey(typeThrow))) {
//                            description = (String) commentInfo.get(typeThrow);
//                        } else if (commentInfo.containsKey(referenceType.toString())) {
//                            description = (String) commentInfo.get(referenceType.toString());
//                        }
////                        else {
////
////                            if (commentString.contains(typeThrow) || commentString.contains(referenceType.toString())) {
////                                if(!typeThrow.equals("")){
////                                    System.out.println("a");
////                                }
////                            }
////                        }
//                        codeExceptionModel.setDescription(description);
//                        exceptionModelList.add(codeExceptionModel);
//                        addCodeExceptionRelation(methodName, exceptionId);
//                        exceptionId++;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//
//    private static void parseVariableDeclarator(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            try {
//                ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
//                String methodName = resolvedMethodDeclaration.getQualifiedSignature();
//                methodName = removeAnonymous(methodName);
//                MethodVarModel methodVarModel = new MethodVarModel();
//                methodVarModel.setMethod_name(methodName);
//                List<VariableDeclarator> children = methodDeclaration.getChildNodesByType(VariableDeclarator.class);
//                List<VariableModel> variableModelList = new ArrayList<>();
//
//                for (VariableDeclarator variableDeclarator : children) {
//                    try {
//                        String valTypeName = getVariableTypeName(variableDeclarator);
//                        ResolvedValueDeclaration d = variableDeclarator.resolve();
//                        String valName = d.getName();
//                        variableModelList.add(new VariableModel(valTypeName, valName));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                methodVarModel.setVariable_model_list(variableModelList);
//                if (variableModelList.size() > 0 && !(methodName.equals(""))) {
//                    methodVarModelArrayList.add(methodVarModel);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    private static void parseFieldInMethod(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            String methodQualifiedName = methodDeclaration.resolve().getQualifiedSignature();
//            String className = methodQualifiedName.substring(0, methodQualifiedName.indexOf(methodDeclaration.getNameAsString()) - 1);
//            List<FieldAccessExpr> fieldAccessExprList = methodDeclaration.getChildNodesByType(FieldAccessExpr.class);
//            for (FieldAccessExpr fieldAccessExpr : fieldAccessExprList) {
//                try {
//                    System.out.println(fieldAccessExpr);
//                    String fieldTypeName = "";
//                    String fieldName = "";
//                    String fieldFullName = "";
////                ResolvedType resolvedType = fieldAccessExpr.resolve().getType();
//                    fieldName = fieldAccessExpr.toString().substring(fieldAccessExpr.toString().indexOf("."));
//                    if (fieldAccessExpr.getScope() instanceof NameExpr) {
//                        try {
//                            ResolvedType resolvedType = ((NameExpr) fieldAccessExpr.getScope()).resolve().getType();
//                            if (resolvedType instanceof ReferenceTypeImpl) {
//                                fieldTypeName = ((NameExpr) fieldAccessExpr.getScope()).resolve().getType().asReferenceType().getQualifiedName();
//                            } else if (resolvedType instanceof ResolvedArrayType) {
//                                fieldTypeName = ((ResolvedArrayType) resolvedType).getComponentType().asReferenceType().getQualifiedName();
//                            } else {
//                                System.out.println("not resolved");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    } else if (fieldAccessExpr.getScope() instanceof ThisExpr) {
//                        fieldTypeName = className;
//                    } else {
//                        System.out.println("x.x.x");
//                    }
//                    fieldFullName = fieldTypeName + fieldName;
//                    addMethodFieldRelationModelList(methodQualifiedName, fieldFullName, fieldTypeName, Tools.Field_In_Method);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//        }
//
//    }
//
//    private static void parseClassUsedInMethod(CompilationUnit cu) {
//        List<MethodDeclaration> methodDeclarationList = cu.findAll(MethodDeclaration.class);
//        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
//            List<MethodCallExpr> methodCallExprList = methodDeclaration.getChildNodesByType(MethodCallExpr.class);
//            for (MethodCallExpr methodCall : methodCallExprList) {
//                try {
//                    String base = methodDeclaration.resolve().getQualifiedSignature();
//                    String belongClass = "";
//                    String methodCallSignature = methodCall.resolve().getQualifiedSignature();
//                    int end = methodCallSignature.indexOf("(");
//                    methodCallSignature = methodCallSignature.substring(0, end);
//                    int pos = methodCallSignature.lastIndexOf(".");
//                    belongClass = methodCallSignature.substring(0, pos);
//                    System.out.println(methodCallSignature);
//                    addRelationModelList(base, belongClass, Tools.RELATION_CATEGORY_METHOD_IMPLEMENT_CODE_CALL_CLASS);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                System.out.println();
//            }
//        }
//    }
//
//    private static void addUsedDir(List<String> dirList, String argoUML24Path, String argoUML262Path, String mucommander085Path, String eclipsePath, String derby_10_9_1_0_path) {
//        dirList.add(argoUML24Path);
//        dirList.add(argoUML262Path);
//        dirList.add(mucommander085Path);
//        dirList.add(eclipsePath);
//        dirList.add(derby_10_9_1_0_path);
//    }
//
//    public static void main(String[] args) {
//        blacklist.add("D:\\project\\derby-10.9.1.0\\db-derby-10.9.1.0-src\\db-derby-10.9.1.0-src\\java\\engine\\org\\apache\\derby\\iapi\\error\\ExceptionSeverity.java");
//        blacklist.add("D:\\project\\derby-10.9.1.0\\db-derby-10.9.1.0-src\\db-derby-10.9.1.0-src\\java\\engine\\org\\apache\\derby\\iapi\\db\\Database.java");
//        blacklist.add("D:\\project\\derby-10.9.1.0\\db-derby-10.9.1.0-src\\db-derby-10.9.1.0-src\\java\\engine\\org\\apache\\derby\\iapi\\reference\\JDBC30Translation.java");
//        blacklist.add("D:\\project\\derby-10.9.1.0\\db-derby-10.9.1.0-src\\db-derby-10.9.1.0-src\\java\\engine\\org\\apache\\derby\\iapi\\reference\\JDBC40Translation.java");
//        blacklist.add("D:\\project\\derby-10.9.1.0\\db-derby-10.9.1.0-src\\db-derby-10.9.1.0-src\\java\\engine\\org\\apache\\derby\\iapi\\reference\\MessageId.java");
//        blacklist.add("D:\\project\\derby-10.9.1.0\\db-derby-10.9.1.0-src\\db-derby-10.9.1.0-src\\java\\engine\\org\\apache\\derby\\impl\\services\\locks\\ConcurrentLockSet.java");
//        blacklist.add("E:\\project\\数据\\jedit42source.tar\\jedit42source\\jEdit\\bsh\\Reflect.java");
//        blacklist.add("E:\\project\\数据\\jedit4.3source.tar\\jEdit\\org\\gjt\\sp\\jedit\\bsh\\Reflect.java");
//        blacklist.add("D:\\实验室\\eclipse-sourceBuild-srcIncluded-3.0\\plugins\\org.eclipse.core.runtime.compatibility\\src-boot\\org\\eclipse\\core\\boot\\IPlatformConfiguration.java");
//        blacklist.add("D:\\实验室\\eclipse-sourceBuild-srcIncluded-3.0\\plugins\\org.eclipse.core.runtime.compatibility\\src-boot\\org\\eclipse\\core\\boot\\IPlatformRunnable.java");
//        blacklist.add("D:\\实验室\\eclipse-sourceBuild-srcIncluded-3.0\\plugins\\org.eclipse.ui.workbench\\Eclipse UI\\org\\eclipse\\ui\\internal\\EditorList.java");
////        String dirPath = mahout_distribution_0_8;
////        String dirPath = ArgoUML22Path;
//        List<String> dirList = new ArrayList<>();
//        addUsedDir(dirList, jEditPath42, jEditPath43, mahout_distribution_0_8, jabrefPath, ArgoUML22Path);
////        addUsedDir(dirList, ArgoUML24Path, ArgoUML262Path, mucommander085Path, eclipsePath, derby_10_9_1_0_Path);
//        for (String dirPath : dirList) {
////        String dirPath = eclipsePath;
//            TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
//            TypeSolver javaParserTypeSolver_1 = new JavaParserTypeSolver(new File(dirPath));
//            reflectionTypeSolver.setParent(reflectionTypeSolver);
//            try {
//                List<JarTypeSolver> jarTypeSolverList = getJarTypeSolver();
//                CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
//                combinedSolver.add(reflectionTypeSolver);
//                combinedSolver.add(javaParserTypeSolver_1);
//                for (JarTypeSolver jarTypeSolver : jarTypeSolverList) {
//                    combinedSolver.add(jarTypeSolver);
//                }
//                JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
//                JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
//                File projectDir = new File(dirPath);
//                List<String> pathList = GetJavaFiles.listClasses(projectDir);
//                for (String path : pathList) {
//                    try {
////                    if (!path.equals(t)) {
////                        continue;
////                    }
//                        appendFile("log/log.txt", path);
//                        System.out.println(path);
//                        if (blacklist.contains(path)) {
//                            continue;
//                        }
//                        CompilationUnit cu = JavaParser.parse(new File(path));
//                        parseClassUsedInMethod(cu);
//                        String packageName = parsePackage(cu);
//                        parseClassInterface(cu, packageName);
//                        parseConstructor(cu);
//                        parseMethodDeclaration(cu);
//                        parseMethodCallRelationWithQualifiedName(cu);
//                        parseMethodParameter(cu);
//                        parseExceptionInfo(cu);
//                        parseField(cu);
//                        parseReturnValue(cu);
//                        parseVariableDeclarator(cu);
//                        parseFieldInMethod(cu);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                startWrite(dirPath);
//                cleanAll();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//
//}
//
