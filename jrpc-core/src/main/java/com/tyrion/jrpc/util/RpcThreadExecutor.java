package com.tyrion.jrpc.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Rpc线程池
 *
 * @author TyrionJ
 * @date 2020/12/15 13:49
 */
@Slf4j
public class RpcThreadExecutor {

    private static ThreadPoolExecutor executor;

    static {
        int threadNumber = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(threadNumber, threadNumber, 0L, TimeUnit.MILLISECONDS,
                // 用来缓冲执行任务的队列容量
                new LinkedBlockingQueue<>(200),
                // rejection-policy：当pool已经达到max size的时候，如何处理新任务
                // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private static void showStatus(String action) {
        log.debug("[RpcThreadExecutor {}] 核心线程数: [{}], 最大线程数: [{}], 当前线程数: [{}], " +
                        "任务总数: [{}], 已完成任务数: [{}], 活跃线程数: [{}], 队列大小: [{}]", action,
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getPoolSize(),
                executor.getTaskCount(),
                executor.getCompletedTaskCount(),
                executor.getActiveCount(),
                executor.getQueue().size());
    }

    public static void execute(Runnable task) {
        showStatus("do execute");
        executor.execute(task);
    }

    public Future<?> submit(Runnable task) {
        showStatus("do submit");
        return executor.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        showStatus("do submit");
        return executor.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T var2) {
        showStatus("do submit");
        return executor.submit(task, var2);
    }
}
