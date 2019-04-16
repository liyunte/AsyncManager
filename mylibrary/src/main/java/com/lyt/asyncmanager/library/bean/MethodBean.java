package com.lyt.asyncmanager.library.bean;

import com.lyt.asyncmanager.library.type.TaskType;

import java.lang.reflect.Method;

public class MethodBean {
    /**
     * 方法参数1
     */
    private Class<?> type;

    /**
     * 注解参数 哪个任务
     */
    private String taskFlag;

    /**
     * 注解参数 任务的生命周期
     */
    private TaskType taskType;

    /**
     * 方法
     */
    private Method method;


    public MethodBean(Class<?> type, String taskFlag, TaskType taskType, Method method) {
        this.type = type;
        this.taskFlag = taskFlag;
        this.taskType = taskType;
        this.method = method;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getTaskFlag() {
        return taskFlag;
    }

    public void setTaskFlag(String taskFlag) {
        this.taskFlag = taskFlag;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
