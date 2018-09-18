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

import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;

/**
 * 请求接口回调的载体
 *
 * @author xuexiang
 * @since 2018/9/17 下午3:39
 */
public class CallbackMail implements Parcelable {

    private long mTimeStamp;

    private int mIndex;

    private MethodWrapper mMethod;

    private ParameterWrapper[] mParameters;

    public static final Creator<CallbackMail> CREATOR
            = new Creator<CallbackMail>() {
        public CallbackMail createFromParcel(Parcel in) {
            CallbackMail mail = new CallbackMail();
            mail.readFromParcel(in);
            return mail;
        }
        public CallbackMail[] newArray(int size) {
            return new CallbackMail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mTimeStamp);
        parcel.writeInt(mIndex);
        parcel.writeParcelable(mMethod, flags);
        parcel.writeParcelableArray(mParameters, flags);
    }

    public void readFromParcel(Parcel in) {
        mTimeStamp = in.readLong();
        mIndex = in.readInt();
        ClassLoader classLoader = CallbackMail.class.getClassLoader();
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

    private CallbackMail() {

    }

    public CallbackMail(long timeStamp, int index, MethodWrapper method, ParameterWrapper[] parameters) {
        mTimeStamp = timeStamp;
        mIndex = index;
        mMethod = method;
        mParameters = parameters;
    }

    public ParameterWrapper[] getParameters() {
        return mParameters;
    }

    public int getIndex() {
        return mIndex;
    }

    public MethodWrapper getMethod() {
        return mMethod;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

}
