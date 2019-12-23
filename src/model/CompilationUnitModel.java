package model;

import com.github.javaparser.ast.CompilationUnit;

import javax.swing.tree.TreeNode;

public class CompilationUnitModel{
    private static String name;
    private static CompilationUnit cu;

    public CompilationUnitModel() {
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
