package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetJavaFiles {
    public static List<String> listClasses(File projectDir) {
        List<String> pathList = new ArrayList<>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> pathList.add(file.getAbsolutePath())).explore(projectDir);
        return pathList;
    }

    public static void main(String[] args) {
        File projectDir = new File("C:\\D\\Document\\Research\\APIDrective\\src");
        System.out.println(listClasses(projectDir));
    }
}
