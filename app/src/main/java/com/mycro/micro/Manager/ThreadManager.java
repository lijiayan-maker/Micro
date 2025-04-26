package com.mycro.micro.Manager;

import android.os.Handler;
import android.os.HandlerThread;

public class ThreadManager {

    private static String Task_Thread;

    private Handler mTaskHandler;//工作线程


    /**
     * 隐藏构造方法
     * 单例模式
     */
    private ThreadManager(){

    }

    private static class SingleTon{
        private static final ThreadManager INSTANCE = new ThreadManager();
    }

    private static ThreadManager instance(){
        return SingleTon.INSTANCE;
    }

    private static synchronized Handler lazyNew(Handler handler, String name){
        if(handler == null){
            HandlerThread handlerThread = new HandlerThread(name);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());

        }
        return handler;
    }

    public static void runOnTask(Runnable runnable){
        instance().mTaskHandler = lazyNew(instance().mTaskHandler,Task_Thread);
        instance().mTaskHandler.post(runnable);
    }

    public static Handler taskHandler() {
        instance().mTaskHandler = lazyNew(instance().mTaskHandler,Task_Thread);
        return instance().mTaskHandler;
    }

}
