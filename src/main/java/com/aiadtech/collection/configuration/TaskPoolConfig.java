package com.aiadtech.collection.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池的配置
 * @author Object
 */
@Configuration
@Slf4j
@ComponentScan("com.aiadtech.collection")
public class TaskPoolConfig implements AsyncConfigurer {

    /**
     * 核心线程数（默认线程数）
     */
    @Value("${thread.core-pool-size}")
    private int corePoolSize;

    /**
     * 最大线程数
     */
    @Value("${thread.max-pool-size}")
    private int maxPoolSize;

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    @Value("${thread.keep-alive-time}")
    private int keepAliveTime;

    /**
     * 缓冲队列大小
     */
    @Value("${thread.queue-capacity}")
    private int queueCapacity;

    /**
     * 线程池名前缀
     */
    private static final String THREAD_NAME_PREFIX = "taskExecutor-";

    /**
     * 场景：非核心、日志、低优先级通知
     * @return
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数目，线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 队列中最大的数目，用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 线程空闲后的最大存活时间，当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(keepAliveTime);
        // 线程名称前缀
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 设置线程池关闭的时候等待所有任务都完成后，再继续销毁其他的Bean,确保异步任务的销毁就会先于数据库连接池对象的销毁。
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中 任务的等待时间
        executor.setAwaitTerminationSeconds(60);
        // 在任务不能再提交的时候，丢弃任务，不抛出异常
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 场景:导出、用户主动触发、需结果反馈
     * @return
     */
    @Bean("exportExecutor")
    public ThreadPoolTaskExecutor exportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数目，线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 队列中最大的数目，用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 线程空闲后的最大存活时间，当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(keepAliveTime);
        // 线程名称前缀
        executor.setThreadNamePrefix("voiceExportExecutor-");
        // 设置线程池关闭的时候等待所有任务都完成后，再继续销毁其他的Bean,确保异步任务的销毁就会先于数据库连接池对象的销毁。
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中 任务的等待时间
        executor.setAwaitTerminationSeconds(60);
        // 在任务不能再提交的时候，抛出异常
        executor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor executor1) -> {
            log.info("exportExecutor is full and task reject {}", r.toString());
            throw new RejectedExecutionException("线程池已满，任务被拒绝: " + r);
        });
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    /**
     * IO 密集、文件上传、外部服务调用
     * @return
     */
    @Bean("ossUploadTreadPool")
    public ThreadPoolTaskExecutor asyncServiceExecutorForOss() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数，采用IO密集 h/(1-拥塞)
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数,由于oss连接数量有限，此处尽力设计大点
        executor.setMaxPoolSize(maxPoolSize);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(keepAliveTime);
        // 设置默认线程名称
        executor.setThreadNamePrefix("ossUploadTask-");
        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        log.info("初始化 ossUploadTreadPool 线程池: 核心线程数={}, 最大线程数={}",
            corePoolSize, maxPoolSize);
        return executor;
    }
}
