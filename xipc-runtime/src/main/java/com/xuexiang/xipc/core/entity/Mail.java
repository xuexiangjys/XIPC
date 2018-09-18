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

package com.xuexiang.xipc.core.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;

import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;

/**
 * 请求接口的方法、参数、数据的载体
 *
 * @author xuexiang
 * @since 2018/9/17 下午3:20
 */
public class Mail implements Parcelable {

    private long mTimeStamp;

    /**
     * 唯一号标识
     */
    private int mPid;

    /**
     * 请求接口的类型
     */
    private ObjectWrapper mObject;

    /**
     * 请求的方法
     */
    private MethodWrapper mMethod;

    /**
     * 请求的参数
     */
    private ParameterWrapper[] mParameters;

    public static final Creator<Mail> CREATOR
            = new Creator<Mail>() {
        public Mail createFromParcel(Parcel in) {
            Mail mail = new Mail();
            mail.readFromParcel(in);
            return mail;
        }

        public Mail[] newArray(int size) {
            return new Mail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mTimeStamp);
        parcel.writeInt(mPid);
        parcel.writeParcelable(mObject, flags);
        parcel.writeParcelable(mMethod, flags);
        parcel.writeParcelableArray(mParameters, flags);
    }

    public void readFromParcel(Parcel in) {
        mTimeStamp = in.readLong();
        mPid = in.readInt();
        ClassLoader classLoader = Mail.class.getClassLoader();
        mObject = in.readParcelable(classLoader);
        mMethod = in.readParcelable(classLoader);
        Parcelable[] parcelables = in.readParcelableArray(classLoader);
        if (parcelables == null) {
            mParameters = null;
        } else {
            int length = parcelables.length;
            mParameters = new ParameterWrapper[length];
            for (int i = 0; i < length; ++i) {
                mParameters[i] = (ParameterWrapper) parcelables[i];
            }
        }

    }

    private Mail() {

    }

    /**
     * 构造放啊
     *
     * @param timeStamp  自增序列
     * @param object     请求类型
     * @param method     请求方法
     * @param parameters 请求参数
     */
    public Mail(long timeStamp, ObjectWrapper object, MethodWrapper method, ParameterWrapper[] parameters) {
        mTimeStamp = timeStamp;
        mPid = Process.myPid();
        mObject = object;
        mMethod = method;
        mParameters = parameters;
    }

    public int getPid() {
        return mPid;
    }

    public ParameterWrapper[] getParameters() {
        return mParameters;
    }

    public ObjectWrapper getObject() {
        return mObject;
    }

    public MethodWrapper getMethod() {
        return mMethod;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

}
