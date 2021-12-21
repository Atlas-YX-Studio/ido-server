package com.bixin.common.utils;

import java.util.concurrent.*;

public class ThreadPoolUtil {
    private final static int THREAD_NUMS = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * FixedThreadPool
     */
    private final static ExecutorService executorService = new ThreadPoolExecutor(THREAD_NUMS, THREAD_NUMS,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static <T> Future<T> submit(Callable<T> callable){
        return executorService.submit(callable);
    }

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public static <T> Future<T> submitRetry(Callable<T> callable, int attempt, long delay, Class<? extends Throwable> exceptionClass) {
        return executorService.submit((Callable<T>) () -> RetryingUtil.retry(callable, attempt, delay, exceptionClass));
    }

    public static void executeRetry(Runnable runnable, int attempt, long delay, Class<? extends Throwable> exceptionClass) {
        executorService.execute(() -> RetryingUtil.retry(runnable, attempt, delay, exceptionClass));
    }

    public static <T> Future<T> submitRetry(Callable<T> callable, Class<? extends Throwable> exceptionClass) {
        return executorService.submit((Callable<T>) () -> RetryingUtil.retry(callable, 3, 1000, exceptionClass));
    }

    public static void executeRetry(Runnable runnable, Class<? extends Throwable> exceptionClass) {
        executorService.execute(() -> RetryingUtil.retry(runnable, 3, 1000, exceptionClass));
    }
}
