package cn.edu.wtu.sj.mycontact.utils;

import android.os.Handler;

public class ThreadUtils {
    //定义一个主线程下一个handler对象
    public static Handler mhandler=new Handler();
    //UI线程下执行task
    public static void runInUIThread(Runnable task){
        mhandler.post(task);
    }
    //启动普通线程
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }
}
