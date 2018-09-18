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

package com.xuexiang.xipc.core.wrapper;

import android.os.Parcel;

/**
 * 基础包装器，用于IPC信息传输
 *
 * @author xuexiang
 * @since 2018/9/14 下午4:14
 */
public class BaseWrapper {

    /**
     * 是不是类的名字【true: 类的名字, false:用注解定义的映射】
     */
    private boolean mIsName;

    /**
     * 名字
     */
    private String mName;

    /**
     * 构造方法
     * @param isName
     * @param name
     */
    protected void setName(boolean isName, String name) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null");
        }
        mIsName = isName;
        mName = name;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mIsName ? 1 : 0);
        parcel.writeString(mName);
    }

    public void readFromParcel(Parcel in) {
        mIsName = in.readInt() == 1;
        mName = in.readString();
    }

    public boolean isName() {
        return mIsName;
    }

    public String getName() {
        return mName;
    }
}
