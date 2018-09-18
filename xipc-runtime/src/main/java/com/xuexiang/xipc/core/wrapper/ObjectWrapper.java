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
import android.os.Parcelable;

import com.xuexiang.xipc.annotation.ClassName;
import com.xuexiang.xipc.util.TimeStampGenerator;
import com.xuexiang.xipc.util.TypeUtils;

/**
 * 对象的包装器（用于方法参数的包装）
 *
 * @author xuexiang
 * @since 2018/9/14 下午6:16
 */
public class ObjectWrapper extends BaseWrapper implements Parcelable {

    /**
     * 请求创建新的实例对象【注册服务】
     */
    public static final int TYPE_OBJECT_TO_NEW = 0;

    /**
     * 请求获取单例
     */
    public static final int TYPE_OBJECT_TO_GET = 1;

    /**
     * 请求获取工具类
     */
    public static final int TYPE_CLASS_TO_GET = 2;

    /**
     * 处理对象方法的执行
     */
    public static final int TYPE_OBJECT = 3;

    /**
     * 处理工具类(静态)方法的执行
     */
    public static final int TYPE_CLASS = 4;

    /**
     * 自增long型，标记对象
     */
    private long mTimeStamp;

    //only used here
    private Class<?> mClass;

    /**
     * 对象的包装类型
     */
    private int mType;

    public static final Creator<ObjectWrapper> CREATOR
            = new Creator<ObjectWrapper>() {
        public ObjectWrapper createFromParcel(Parcel in) {
            ObjectWrapper objectWrapper = new ObjectWrapper();
            objectWrapper.readFromParcel(in);
            return objectWrapper;
        }

        public ObjectWrapper[] newArray(int size) {
            return new ObjectWrapper[size];
        }
    };

    private ObjectWrapper() {
    }

    /**
     * 包装对象
     *
     * @param clazz 对象的类
     * @param type  对象的类型
     */
    public ObjectWrapper(Class<?> clazz, int type) {
        setName(!clazz.isAnnotationPresent(ClassName.class), TypeUtils.getClassId(clazz));
        mClass = clazz;
        mTimeStamp = TimeStampGenerator.getTimeStamp();
        mType = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeLong(mTimeStamp);
        parcel.writeInt(mType);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mTimeStamp = in.readLong();
        mType = in.readInt();
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public Class<?> getObjectClass() {
        return mClass;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }
}
