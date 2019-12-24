package model;

import javafx.util.Pair;

import java.util.List;

public class TempMethod {
    private String description;
    private String methodName;
    private String name;
    // params name, params description
    private List<Pair<String, String>> paramsTag;
    private List<Pair<String, String>> throwsTag;
    private List<String> returnTag;
    private String belongClass;

    public TempMethod(){

    }
    public String getDescription() { return description; }
    public void setDescription(String description){this.description = description;}

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName){this.methodName = methodName;}

    public String getName() { return name; }
    public void setName(String name){this.name = name;}

    public void setParamsTag(List<Pair<String, String>> paramsTag){this.paramsTag = paramsTag;}
    public List<Pair<String, String>> getParamsTag(){return paramsTag;}

    public void setThrowsTag(List<Pair<String, String>> throwsTag){this.throwsTag = throwsTag;}
    public List<Pair<String, String>> getThrowsTag(){ return throwsTag; }

    public void setReturnTag(List<String> returnTag){this.returnTag = returnTag;}
    public List<String> getReturnTag(){ return returnTag; }

    public void setBelongClass(String belongClass){this.belongClass = belongClass;}
    public String getBelongClass(){return belongClass; }

}
