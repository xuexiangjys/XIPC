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

import com.xuexiang.xipc.logs.IPCLog;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象存储仓库，存放对应对象的映射
 *
 * @author xuexiang
 * @since 2018/9/17 下午4:06
 */
public class ObjectCenter {

    private static volatile ObjectCenter sInstance = null;

    private final ConcurrentHashMap<Long, Object> mObjects;

    private ObjectCenter() {
        mObjects = new ConcurrentHashMap<>();
    }

    public static ObjectCenter getInstance() {
        if (sInstance == null) {
            synchronized (ObjectCenter.class) {
                if (sInstance == null) {
                    sInstance = new ObjectCenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 取出对象
     *
     * @param timeStamp
     * @return
     */
    public Object getObject(Long timeStamp) {
        return mObjects.get(timeStamp);
    }

    /**
     * 存放对象
     *
     * @param timeStamp
     * @param object
     */
    public void putObject(long timeStamp, Object object) {
        mObjects.put(timeStamp, object);
    }

    /**
     * 删除对象
     *
     * @param timeStamps
     */
    public void deleteObjects(List<Long> timeStamps) {
        for (Long timeStamp : timeStamps) {
            if (mObjects.remove(timeStamp) == null) {
                IPCLog.e("[ObjectCenter] An error occurs in the GC.");
            }
        }
    }
}
