package model;

import java.util.List;

public class ClassModel {
    private String qualified_name;
    private Integer type;
    private String code = "";

    private String comment;
    private String commentList;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentList() {
        return commentList;
    }

    public void setCommentList(String commentList) {
        this.commentList = commentList;
    }

    public ClassModel() {
    }

    public String getQualified_name() {
        return qualified_name;
    }

    public void setQualified_name(String qualified_name) {
        this.qualified_name = qualified_name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

