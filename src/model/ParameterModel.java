package model;

public class ParameterModel {
    private String parameter_type;
    private String parameter_name;
    private Integer id;
    private String description;

    public ParameterModel(String parameter_type, String parameter_name, Integer id, String description) {
        this.parameter_type = parameter_type;
        this.parameter_name = parameter_name;
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ParameterModel() {
    }

    public ParameterModel(String parameter_type, String parameter_name, Integer id) {
        this.parameter_type = parameter_type;
        this.parameter_name = parameter_name;
        this.id = id;
    }

    public String getParameter_type() {
        return parameter_type;
    }

    public void setParameter_type(String parameter_type) {
        this.parameter_type = parameter_type;
    }

    public String getParameter_name() {
        return parameter_name;
    }

    public void setParameter_name(String parameter_name) {
        this.parameter_name = parameter_name;
    }
}
