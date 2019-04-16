package com.lyt.asyncmanager.library.annotation;


import com.lyt.asyncmanager.library.type.TaskType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
    TaskType taskType() default TaskType.AUTO;//生命周期
    String  taskFlag() default "";//标志执行的任务
}
