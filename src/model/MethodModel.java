package model;

import java.util.List;

public class MethodModel {
    private String method_name;
    private List<VariableModel> variable_model_list;

    public List<VariableModel> getVariable_model_list() {
        return variable_model_list;
    }

    public void setVariable_model_list(List<VariableModel> variable_model_list) {
        this.variable_model_list = variable_model_list;
    }

    public MethodModel() {
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }
}
