package com.lyt.asyncmanager.library;

import android.os.AsyncTask;


public  class  AsyncTaskImpl extends AsyncTask {

    private IAsyncCallback listener;
    public AsyncTaskImpl(IAsyncCallback listener) {
        this.listener = listener;
    }
    /**
     *  // 方法1：onPreExecute（）
     // 作用：执行 线程任务前的操作
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected Object doInBackground(Object[] objects) {
        if (listener!=null){
           listener.doInBackground();
        }
        return null;
    }
    // 方法3：onProgressUpdate（）
    // 作用：在主线程 显示线程任务执行的进度
    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    // 方法4：onPostExecute（）
    // 作用：接收线程任务执行结果、将执行结果显示到UI组件

    @Override
    protected void onPostExecute(Object result) {
        // 执行完毕后，则更新UI
        if (listener!=null){
            listener.onPostExecute();
        }
    }

    // 方法5：onCancelled()
    // 作用：将异步任务设置为：取消状态
    @Override
    protected void onCancelled() {
        if (listener!=null){
            listener.onCancelled();
        }
    }

}
