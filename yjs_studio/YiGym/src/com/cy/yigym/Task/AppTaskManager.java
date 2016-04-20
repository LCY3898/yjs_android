package com.cy.yigym.Task;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lijianqiang on 15/11/21.
 */
public class AppTaskManager {
    /** 总共多少任务 */
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    private static final String TAG = "AppTaskManager";

    private BlockingQueue<Runnable> queue;

    private ThreadPoolExecutor executor;

    private static AppTaskManager instance;

    private AppTaskManager() {
        queue = new LinkedBlockingQueue<Runnable>();
        int corePoolSize = getThreadCount();
        int maximumPoolSize = corePoolSize * 2 + 1;
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 200, TimeUnit.MILLISECONDS, queue);
        Log.d(TAG, "corePoolSize:" + corePoolSize + "&maximumPoolSize:" + maximumPoolSize + "&cupget:" + THREAD_COUNT);
    }

    public static AppTaskManager getInstance() {
        if (instance == null) {
            instance = new AppTaskManager();
            //AppRuntimeInfo.setDeviceCpu(String.valueOf(THREAD_COUNT));
        }

        return instance;
    }

    public static void execute(Runnable r) {
        getInstance().getExecutor().execute(r);
    }

    public static void shutdown() {
        getInstance().getExecutor().shutdown();
    }

    /**
     * @return Returns the queue.
     */
    public BlockingQueue<Runnable> getQueue() {
        return queue;
    }

    /**
     * @param queue The queue to set.
     */
    public void setQueue(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    /**
     * @return Returns the executor.
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    /**
     * @param executor The executor to set.
     */
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    /**
     * @return Returns the threadCount.
     */
    public static int getThreadCount() {
        int c = THREAD_COUNT;
        if (c > 8) {
            c = 8;
        }
        if (c < 3) {
            c = 3;
        }
        return c;
    }
}
