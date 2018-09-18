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

import android.os.RemoteException;
import android.support.v4.util.Pair;

import com.xuexiang.xipc.core.channel.IIPCServiceCallback;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPC回调接口的资源回收
 * <p>
 * This works in the main process.
 */
public class IPCCallbackGc {

    private static volatile IPCCallbackGc sInstance = null;
    /**
     * 引用队列
     */
    private final ReferenceQueue<Object> mReferenceQueue;

    /**
     * 引用信息存储
     */
    private final ConcurrentHashMap<PhantomReference<Object>, Triple<IIPCServiceCallback, Long, Integer>> mTimeStamps;

    private IPCCallbackGc() {
        mReferenceQueue = new ReferenceQueue<>();
        mTimeStamps = new ConcurrentHashMap<>();
    }

    public static IPCCallbackGc getInstance() {
        if (sInstance == null) {
            synchronized (IPCCallbackGc.class) {
                if (sInstance == null) {
                    sInstance = new IPCCallbackGc();
                }
            }
        }
        return sInstance;
    }

    /**
     * 资源回收
     */
    private void gc() {
        synchronized (mReferenceQueue) {
            PhantomReference<Object> reference;
            Triple<IIPCServiceCallback, Long, Integer> triple;
            HashMap<IIPCServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>> timeStamps
                    = new HashMap<>();
            while ((reference = (PhantomReference<Object>) mReferenceQueue.poll()) != null) {
                triple = mTimeStamps.remove(reference);
                if (triple != null) {
                    Pair<ArrayList<Long>, ArrayList<Integer>> tmp = timeStamps.get(triple.first);
                    if (tmp == null) {
                        tmp = new Pair<>(new ArrayList<Long>(), new ArrayList<Integer>());
                        timeStamps.put(triple.first, tmp);
                    }
                    tmp.first.add(triple.second);
                    tmp.second.add(triple.third);
                }
            }
            Set<Map.Entry<IIPCServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>>> set = timeStamps.entrySet();
            for (Map.Entry<IIPCServiceCallback, Pair<ArrayList<Long>, ArrayList<Integer>>> entry : set) {
                Pair<ArrayList<Long>, ArrayList<Integer>> values = entry.getValue();
                if (!values.first.isEmpty()) {
                    try {
                        entry.getKey().gc(values.first, values.second);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 注册IPC接口回调，以方便资源回收
     *
     * @param callback  回调
     * @param object    回调接口的返回值
     * @param timeStamp 序号
     * @param index     索引
     */
    public void register(IIPCServiceCallback callback, Object object, long timeStamp, int index) {
        gc();
        //使用虚引用
        mTimeStamps.put(new PhantomReference<>(object, mReferenceQueue), Triple.create(callback, timeStamp, index));
    }
}
