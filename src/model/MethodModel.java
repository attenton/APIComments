package model;

import java.lang.reflect.Parameter;
import java.util.List;

public class MethodModel {
    private String method_name;
    private Integer type;
    private String type_return;
    private String full_declaration;
    private String description;
    private String comment;
    private List<String> inside_comment;
    private List<String> thrown_exceptions;
    private List<String> parameter_model_list;
    private List<String> string_literal_expr;
    private List<String> modifier_list;

    public List<String> getInside_comment() {
        return inside_comment;
    }

    public void setInside_comment(List<String> inside_comment) {
        this.inside_comment = inside_comment;
    }

    public List<String> getString_literal_expr() {
        return string_literal_expr;
    }

    public void setString_literal_expr(List<String> string_literal_expr) {
        this.string_literal_expr = string_literal_expr;
    }

    public List<String> getThrown_exceptions() {
        return thrown_exceptions;
    }

    public void setThrown_exceptions(List<String> thrown_exceptions) {
        this.thrown_exceptions = thrown_exceptions;
    }

    public List<String> getParameter_model_list() {
        return parameter_model_list;
    }

    public void setVariable_model_list(List<String> parameter_model_list) {
        this.parameter_model_list = parameter_model_list;
    }

    public List<String> getModifier_list() { return modifier_list; }

    public void setModifier_list(List<String> modifier_list) {
        this.modifier_list = modifier_list;
    }

    public MethodModel() {
    }

    public String getMethod_name() { return method_name; }

    public void setMethod_name(String method_name) { this.method_name = method_name; }

    public String getType_reutrn() { return type_return; }

    public void setType_return(String type_return) { this.type_return = type_return; }

    public String getFull_declaration() { return full_declaration; }

    public void setFull_declaration(String full_declaration) { this.full_declaration = full_declaration; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Integer getType() { return type; }

    public void setType(Integer type) { this.type = type; }
}
