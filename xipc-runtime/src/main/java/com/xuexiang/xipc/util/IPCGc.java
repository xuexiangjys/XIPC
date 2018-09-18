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

import com.xuexiang.xipc.core.channel.Channel;
import com.xuexiang.xipc.core.channel.IPCService;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPC资源回收
 *
 * This works in the remote process.
 */
public class IPCGc {

    private static volatile IPCGc sInstance = null;

    private final ReferenceQueue<Object> mReferenceQueue;

    private static final Channel CHANNEL = Channel.getInstance();

    private final ConcurrentHashMap<PhantomReference<Object>, Long> mTimeStamps;

    private final ConcurrentHashMap<Long, Class<? extends IPCService>> mServices;

    private IPCGc() {
        mReferenceQueue = new ReferenceQueue<>();
        mTimeStamps = new ConcurrentHashMap<>();
        mServices = new ConcurrentHashMap<>();
    }

    public static IPCGc getInstance() {
        if (sInstance == null) {
            synchronized (IPCGc.class) {
                if (sInstance == null) {
                    sInstance = new IPCGc();
                }
            }
        }
        return sInstance;
    }

    private void gc() {
        synchronized (mReferenceQueue) {
            PhantomReference<Object> reference;
            Long timeStamp;
            HashMap<Class<? extends IPCService>, ArrayList<Long>> timeStamps
                    = new HashMap<>();
            while ((reference = (PhantomReference<Object>) mReferenceQueue.poll()) != null) {
                //After a long time, the program can reach here.
                timeStamp = mTimeStamps.remove(reference);
                if (timeStamp != null) {
                    Class<? extends IPCService> clazz = mServices.remove(timeStamp);
                    if (clazz != null) {
                        ArrayList<Long> tmp = timeStamps.get(clazz);
                        if (tmp == null) {
                            tmp = new ArrayList<>();
                            timeStamps.put(clazz, tmp);
                        }
                        tmp.add(timeStamp);
                    }
                }
            }
            Set<Map.Entry<Class<? extends IPCService>, ArrayList<Long>>> set = timeStamps.entrySet();
            for (Map.Entry<Class<? extends IPCService>, ArrayList<Long>> entry : set) {
                ArrayList<Long> values = entry.getValue();
                if (!values.isEmpty()) {
                    CHANNEL.gc(entry.getKey(), values);
                }
            }
        }
    }

    /**
     * 注册IPC服务接口，以方便资源回收
     * @param service ipc服务
     * @param object
     * @param timeStamp
     */
    public void register(Class<? extends IPCService> service, Object object, Long timeStamp) {
        gc();
        mTimeStamps.put(new PhantomReference<>(object, mReferenceQueue), timeStamp);
        mServices.put(timeStamp, service);
    }
}
