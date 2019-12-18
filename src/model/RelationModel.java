package model;

public class RelationModel {
    private String start_name;
    private String end_name;
    private Integer relation_type;

    public String getStart_name() {
        return start_name;
    }

    public void setStart_name(String start_name) {
        this.start_name = start_name;
    }

    public String getEnd_name() {
        return end_name;
    }

    public void setEnd_name(String end_name) {
        this.end_name = end_name;
    }

    public Integer getRelation_type() {
        return relation_type;
    }

    public void setRelation_type(Integer relation_type) {
        this.relation_type = relation_type;
    }

    public RelationModel() {
    }
}
