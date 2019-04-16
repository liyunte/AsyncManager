package com.lyt.asyncmanager.library;

import android.os.AsyncTask;

import com.lyt.asyncmanager.library.annotation.Task;
import com.lyt.asyncmanager.library.bean.MethodBean;
import com.lyt.asyncmanager.library.bean.TaskBean;
import com.lyt.asyncmanager.library.type.TaskType;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AsyncManager {
    private static volatile AsyncManager instance;
    private Map<Object, List<MethodBean>> networkList;
    private Map<Object, Map<String, AsyncTaskImpl>> workList;

    private AsyncManager() {
        networkList = new HashMap<>();
        workList = new HashMap<>();
    }

    public static AsyncManager getInstance() {
        if (instance == null) {
            synchronized (AsyncManager.class) {
                if (instance == null) {
                    instance = new AsyncManager();
                }
            }
        }
        return instance;
    }

    /**
     * 注册
     *
     * @param register
     */
    public void register(Object register) {
        if (register == null) {
            throw new NullPointerException("注册的对象不能为空");
        }
        List<MethodBean> methodList = networkList.get(register);
        if (methodList == null) {
            methodList = findAnnotationMethod(register);
            networkList.put(register, methodList);
        }

    }

    /**
     * 注销
     *
     * @param register
     */
    public void unRegister(Object register) {
        if (register == null) {
            throw new NullPointerException("注册的对象不能为空");
        }
        if (!networkList.isEmpty()) {
            networkList.remove(register);
        }
        if (!workList.isEmpty()) {
            Map<String, AsyncTaskImpl> work = workList.get(register);
            if (work != null) {
                for (String key : work.keySet()) {
                    AsyncTaskImpl asyncTask = work.get(key);
                    if (asyncTask != null) {
                        if (!asyncTask.isCancelled() && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                            asyncTask.cancel(true);
                        }
                        work.remove(key);
                    }
                }
                workList.remove(register);
            }
        }
    }

    /**
     * 注销全部
     */
    public void unRegisterAll() {
        if (!networkList.isEmpty()) {
            networkList.clear();
        }
        if (!workList.isEmpty()) {
            for (Object o : workList.keySet()) {
                unRegister(o);
            }
        }

    }

    /**
     * 执行任务
     *
     * @param flag 任务标志
     */
    public void execute(String flag) {
        if (flag == null) {
            throw new NullPointerException("flag is not Null");
        }
        AsyncTaskImpl impl = new AsyncTaskImpl(new AsyncCallbackImpl(this, flag));
        impl.execute("");
    }

    /**
     * 执行任务
     *
     * @param register 上下文对象
     * @param flag     任务标志
     */
    public void execute(Object register, String flag) {
        if (register == null) {
            throw new NullPointerException("register is not Null");
        }
        if (flag == null) {
            throw new NullPointerException("flag is not Null");
        }
        Map<String, AsyncTaskImpl> task = workList.get(register);
        AsyncTaskImpl impl = new AsyncTaskImpl(new AsyncCallbackImpl(this, flag));
        if (task == null) {
            task = new HashMap<>();
            task.put(flag, impl);
            workList.put(register, task);
        } else {
            if (task.get(flag) == null) {
                task.put(flag, impl);
                workList.remove(register);
                workList.put(register, task);
            }
        }

        impl.execute("");
    }

    /**
     * 取消单一任务
     *
     * @param register
     * @param flag
     */
    public void cancel(Object register, String flag) {
        if (register == null) {
            throw new NullPointerException("register is not Null");
        }
        if (flag == null) {
            throw new NullPointerException("flag is not Null");
        }
        if (!workList.isEmpty()) {
            Map<String, AsyncTaskImpl> task = workList.get(register);
            if (task != null) {
                AsyncTaskImpl asyncTask = task.get(flag);
                if (asyncTask != null) {
                    if (!asyncTask.isCancelled() && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                        asyncTask.cancel(true);
                    }
                    task.remove(flag);
                }
            }
        }

    }

    private static class AsyncCallbackImpl implements IAsyncCallback {
        private TaskBean taskBean;
        private WeakReference<AsyncManager> weakReference;

        public AsyncCallbackImpl(AsyncManager asyncManager, String flag) {
            weakReference = new WeakReference<>(asyncManager);
            taskBean = new TaskBean(TaskType.AUTO, flag);
        }

        @Override
        public void doInBackground() {
            taskBean.setType(TaskType.WORK);
            if (weakReference != null) {
                weakReference.get().post(taskBean);
            }
        }

        @Override
        public void onPostExecute() {
            taskBean.setType(TaskType.FINISH);
            if (weakReference != null) {
                weakReference.get().post(taskBean);
            }
        }

        @Override
        public void onCancelled() {
            taskBean.setType(TaskType.FINISH);
            if (weakReference != null) {
                weakReference.get().post(taskBean);
            }
        }
    }

    /**
     * 网络事件分发
     *
     * @param response
     */
    private void post(TaskBean response) {
        if (response != null) {
            Set<Object> set = networkList.keySet();
            for (Object getter : set) {
                List<MethodBean> methodList = networkList.get(getter);
                if (methodList != null) {
                    for (MethodBean method : methodList) {
                        if (method.getType().isAssignableFrom(response.getClass())) {
                            if (method.getTaskFlag().isEmpty()) {
                                if (method.getTaskType() == TaskType.AUTO) {
                                    invoke(method, getter, response);
                                } else if (method.getTaskType() == response.getType()) {
                                    invoke(method, getter, response);
                                }
                            } else if (method.getTaskFlag().equalsIgnoreCase(response.getFlag())) {
                                if (method.getTaskType() == TaskType.AUTO) {
                                    invoke(method, getter, response);
                                } else if (method.getTaskType() == response.getType()) {
                                    invoke(method, getter, response);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 查找register中所有使用@Network注解的方法，封装成MethodManager并保存到集合中
     *
     * @param register
     * @return
     */
    private List<MethodBean> findAnnotationMethod(Object register) {
        List<MethodBean> methodList = new ArrayList<>();
        Class<?> clazz = register.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            //方法注解的校验
            Task callBack = method.getAnnotation(Task.class);
            if (callBack == null) {
                continue;
            }
            //方法返回类型检查
            Type returnType = method.getGenericReturnType();
            if (!"void".equalsIgnoreCase(returnType.toString())) {
                throw new RuntimeException("方法返回不是void");
            }
            //方法参数校验
            Class<?>[] parmeterTypes = method.getParameterTypes();
            if (parmeterTypes.length != 1) {
                throw new RuntimeException(method.getName() + "方法有且只有一个");
            }
            if (!parmeterTypes[0].isAssignableFrom(TaskBean.class)) {
                throw new RuntimeException(method.getName() + "方法参数类型不匹配");
            }
            MethodBean methodManager = new MethodBean(parmeterTypes[0], callBack.taskFlag(), callBack.taskType(), method);
            methodList.add(methodManager);

        }
        return methodList;
    }

    /**
     * 反射执行方法
     *
     * @param method
     * @param getter
     * @param response
     */
    private void invoke(MethodBean method, Object getter, TaskBean response) {
        try {
            method.getMethod().invoke(getter, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
