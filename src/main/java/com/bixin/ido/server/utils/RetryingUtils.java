package com.bixin.ido.server.utils;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RetryingUtils {

    public static <T> T retry(Callable<T> callable, int attempt, long delay) throws Exception {
        try {
            return RetryingUtils.<T>buildRetryer(attempt, delay).call(callable);
        } catch (RetryException e) {
            log.info("retry exception:", e.getCause());
            throw (Exception) e.getCause();
        }
    }

    public static void retry(Runnable runnable, int attempt, long delay) {
        try {
            RetryingUtils.buildRetryer(attempt, delay).call(() -> {
                runnable.run();
                return null;
            });
        } catch (RetryException e) {
            log.info("retry exception:", e.getCause());
        } catch (Exception e) {
            log.info("retry exception:", e);
        }
    }

    public static <T> Retryer<T> buildRetryer(int attempt, long delay) {
        return RetryerBuilder.<T>newBuilder()
                .retryIfException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(attempt))
                .withWaitStrategy(WaitStrategies.fixedWait(delay, TimeUnit.MILLISECONDS))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            log.info("retry time: {}, error: {}",
                                    attempt.getAttemptNumber(), attempt.getExceptionCause().toString());
                        }
                    }
                })
                .build();
    }
}
