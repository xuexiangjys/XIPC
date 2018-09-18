/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xipc.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池管理
 *
 * @author xuexiang
 * @since 2018/8/15 上午2:42
 */
public class IPCPoolExecutor {

    private static volatile IPCPoolExecutor sInstance;
    /**
     * 线程池
     */
    private ExecutorService mExecutorService;

    private IPCPoolExecutor() {
        mExecutorService = Executors.newFixedThreadPool(2);
    }

    /**
     * 获取线程池管理
     *
     * @return
     */
    public static IPCPoolExecutor get() {
        if (sInstance == null) {
            synchronized (IPCPoolExecutor.class) {
                if (sInstance == null) {
                    sInstance = new IPCPoolExecutor();
                }
            }
        }
        return sInstance;
    }

    /**
     * 设置线程池
     *
     * @param service 线程池
     */
    public void setExecutorService(final ExecutorService service) {
        if (!mExecutorService.isShutdown()) {
            mExecutorService.shutdownNow();
        }
        mExecutorService = service;
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task 任务
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回null结果。
     */
    public Future<?> addTask(final Runnable task) {
        return mExecutorService.submit(task);
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task   任务
     * @param result 返回的结果
     * @param <T>    泛型
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public <T> Future<T> addTask(final Runnable task, final T result) {
        return mExecutorService.submit(task, result);
    }

    /**
     * 提交一个Callable任务用于执行
     * <p>如果想立即阻塞任务的等待，则可以使用{@code result = exec.submit(aCallable).get();}形式的构造。</p>
     *
     * @param task 任务
     * @param <T>  泛型
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public <T> Future<T> addTask(final Callable<T> task) {
        return mExecutorService.submit(task);
    }

}
