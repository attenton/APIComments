package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Tools {
    public static final Integer PACKAGE_ENTITY = 1;
    public static final Integer CLASS_ENTITY = 2;
    public static final Integer INTERFACE_ENTITY = 3;
    public static final Integer METHOD_ENTITY = 4;
    public static final Integer FIELD_ENTITY = 5;
    public static final Integer VARIABLE_ENTITY = 6;


    public static final Integer BELONGTO = 1;
    public static final Integer EXTEND = 2;
    public static final Integer IMPLEMENT = 3;
    public static final Integer Has_Parameter = 4;
    public static final Integer Method_Call = 13;
    public static final Integer EXCEPTION_THROW = 6;
    public static final Integer Field_In_Method = 15;
    public static final Integer RELATION_CATEGORY_METHOD_IMPLEMENT_CODE_CALL_CLASS = 14;
    public static final Integer Field_In_Class = 14;

//    public static String ImportPath = "C:\\D\\Document\\Research\\APIDrective\\src";
    public static String ImportPath = "C:\\D\\Document\\Research\\APIDrective\\android_sdk_source_code";
//    public static String ImportPath = "C:\\D\\Document\\Research\\APIDrective\\src\\java\\util";

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

}
