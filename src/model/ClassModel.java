package model;

import java.util.List;

public class ClassModel {
    private String qualified_name;
    private Integer type;
    private String description = "";
    private String name = "";
    private String comment;
//    private String commentList;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public String getCommentList() {
//        return commentList;
//    }

//    public void setCommentList(String commentList) {
//        this.commentList = commentList;
//    }

    public ClassModel() {
    }

    public String getQualified_name() {
        return qualified_name;
    }

    public void setQualified_name(String qualified_name) {
        this.qualified_name = qualified_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

