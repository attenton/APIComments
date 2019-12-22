package model;

public class FieldModel {
    private Integer id;
    private String field_type;
    private String field_name;
    private String full_declaration;
    private String comment;

    public FieldModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public void setFull_declaration(String full_declaration) {
        this.full_declaration = full_declaration;
    }

    public String getFull_declaration() {
        return full_declaration;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
