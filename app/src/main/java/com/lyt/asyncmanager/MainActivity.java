package com.lyt.asyncmanager;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lyt.asyncmanager.library.AsyncManager;
import com.lyt.asyncmanager.library.IAsyncCallback;
import com.lyt.asyncmanager.library.annotation.Task;
import com.lyt.asyncmanager.library.bean.TaskBean;
import com.lyt.asyncmanager.library.type.TaskType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AsyncManager.getInstance().register(this);
        AsyncManager.getInstance().execute(this,"1");
        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncManager.getInstance().cancel(MainActivity.this,"1");
            }
        });
    }

    @Task(taskFlag = "1",taskType = TaskType.WORK)
    public void run2(TaskBean taskBean){
        Log.e("liyunte","run2"+taskBean.toString());
        if (taskBean.getType()==TaskType.WORK){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Task(taskFlag = "1",taskType = TaskType.FINISH)
    public void run3(TaskBean taskBean){
        Log.e("liyunte","run3"+taskBean.toString());
    }
    @Task(taskType = TaskType.AUTO)
    public void run4(TaskBean taskBean){
        Log.e("liyunte","run4"+taskBean.toString());
        if (taskBean.getType()==TaskType.WORK){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AsyncManager.getInstance().unRegister(this);
    }
}
