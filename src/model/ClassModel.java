package model;

import java.util.List;

public class ClassModel {
    private String qualified_name;
    private Integer type;
    private String code = "";
    private String declare = "";
    private String permission = "";
    private List<String> string_literal_expr;
    private String type_return;
    private String type_exception;
    private String comment;
    private List<String> inside_comment;
    private List<String> thrown_exceptions;
    private List<String> modifier_list;

    public List<String> getString_literal_expr() {
        return string_literal_expr;
    }

    public void setString_literal_expr(List<String> string_literal_expr) {
        this.string_literal_expr = string_literal_expr;
    }

    public List<String> getModifier_list() {
        return modifier_list;
    }

    public void setModifier_list(List<String> modifier_list) {
        this.modifier_list = modifier_list;
    }

    public List<String> getThrown_exceptions() {
        return thrown_exceptions;
    }

    public void setThrown_exceptions(List<String> thrown_exceptions) {
        this.thrown_exceptions = thrown_exceptions;
    }

    public String getDeclare() {
        return declare;
    }

    public void setDeclare(String declare) {
        this.declare = declare;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    public String getType_return() {
        return type_return;
    }

    public void setType_return(String type_return) {
        this.type_return = type_return;
    }

    public String getType_exception() {
        return type_exception;
    }

    public void setType_exception(String type_exception) {
        this.type_exception = type_exception;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getInside_comment() {
        return inside_comment;
    }

    public void setInside_comment(List<String> inside_comment) {
        this.inside_comment = inside_comment;
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

