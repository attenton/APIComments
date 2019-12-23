package model;

public class ReturnTypeModel {
    private Integer id;
    private String return_value_type;
//    private String return_name;
    private String description;

    public ReturnTypeModel(Integer id, String return_value_type, String description) {
        this.id = id;
        this.return_value_type = return_value_type;
        this.description = description;
    }

    public ReturnTypeModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReturn_value_type() {
        return return_value_type;
    }

    public void setReturn_value_type(String return_value_type) {
        this.return_value_type = return_value_type;
    }

//    public String getReturn_name() { return return_name; }
//
//    public void setReturn_name(String return_name) { this.return_name = return_name; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
