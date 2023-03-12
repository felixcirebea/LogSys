package ro.siit.logsys.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    private static final int MAX_THREADS = 4;
    private static final int QUEUE_SIZE = 100;

    @Bean("deliveryThread")
    public ThreadPoolExecutor deliveryThreadExecutor() {
        return new ThreadPoolExecutor(
                MAX_THREADS,
                MAX_THREADS,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
