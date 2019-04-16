package com.lyt.asyncmanager.library;

public interface IAsyncCallback {
    void doInBackground();//耗时任务
    void onPostExecute();//耗时执行成功后走的方法
    void onCancelled();//取消
}
