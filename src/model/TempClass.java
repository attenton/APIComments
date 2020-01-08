package model;

import java.util.List;
import java.util.Queue;

public class TempClass {
    private String description;
    private Queue<String> inherit;
    private String name;
    private Boolean type; // false class true interface

    public void setDescription(String description){this.description = description;}
    public String getDescription(){return description;}

    public void setInherit(Queue<String> inherit){this.inherit = inherit;}
    public Queue<String> getInherit(){return inherit;}

    public void setType(boolean type){this.type = type;}
    public Boolean getType(){return type;}

    public void setName(String name){this.name = name;}
    public String getName(){return name;}
}
