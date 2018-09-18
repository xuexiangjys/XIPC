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

import com.xuexiang.xipc.core.wrapper.ParameterWrapper;
import com.xuexiang.xipc.core.wrapper.TypeWrapper;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.SerializeUtils;
import com.xuexiang.xipc.util.TypeCenter;

/**
 * 请求结果返回的载体
 *
 * @author xuexiang
 * @since 2018/9/17 下午3:39
 */
public class Reply implements Parcelable {

    private final static TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    /**
     * 请求错误码
     */
    private int mErrorCode;

    /**
     * 请求错误信息
     */
    private String mErrorMessage;

    /**
     * 请求返回类型
     */
    private TypeWrapper mClass;

    /**
     * 请求返回结果
     */
    private Object mResult;

    public static final Creator<Reply> CREATOR
            = new Creator<Reply>() {
        public Reply createFromParcel(Parcel in) {
            Reply reply = new Reply();
            reply.readFromParcel(in);
            return reply;
        }

        public Reply[] newArray(int size) {
            return new Reply[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mErrorCode);
        parcel.writeString(mErrorMessage);
        parcel.writeParcelable(mClass, flags);
        try {
            parcel.writeString(SerializeUtils.encode(mResult));
        } catch (IPCException e) {
            e.printStackTrace();
        }
    }


    public void readFromParcel(Parcel in) {
        mErrorCode = in.readInt();
        ClassLoader classLoader = Reply.class.getClassLoader();
        mErrorMessage = in.readString();
        mClass = in.readParcelable(classLoader);
        try {
            Class<?> clazz = TYPE_CENTER.getClassType(mClass);
            mResult = SerializeUtils.decode(in.readString(), clazz);
        } catch (IPCException e) {
            e.printStackTrace();
        }
    }

    private Reply() {

    }

    /**
     * 请求成功的回复
     *
     * @param parameterWrapper
     */
    public Reply(ParameterWrapper parameterWrapper) {
        try {
            Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);
            mResult = SerializeUtils.decode(parameterWrapper.getData(), clazz);
            mErrorCode = ErrorCodes.SUCCESS;
            mErrorMessage = null;
            mClass = new TypeWrapper(clazz);
        } catch (IPCException e) {
            e.printStackTrace();
            mErrorCode = e.getErrorCode();
            mErrorMessage = e.getMessage();
            mResult = null;
            mClass = null;
        }
    }

    /**
     * 出错的回复
     *
     * @param errorCode
     * @param message
     */
    public Reply(int errorCode, String message) {
        mErrorCode = errorCode;
        mErrorMessage = message;
        mResult = null;
        mClass = null;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public boolean success() {
        return mErrorCode == ErrorCodes.SUCCESS;
    }

    public String getMessage() {
        return mErrorMessage;
    }

    public Object getResult() {
        return mResult;
    }
}
