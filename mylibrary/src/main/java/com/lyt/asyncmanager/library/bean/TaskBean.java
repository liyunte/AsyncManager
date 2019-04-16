package com.lyt.asyncmanager.library.bean;

import com.lyt.asyncmanager.library.type.TaskType;

public class TaskBean {
    private TaskType type;
    private String flag;

    public TaskBean(TaskType type, String flag) {
        this.type = type;
        this.flag = flag;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "type:["+type+"]flag:["+flag+"]";
    }
}
