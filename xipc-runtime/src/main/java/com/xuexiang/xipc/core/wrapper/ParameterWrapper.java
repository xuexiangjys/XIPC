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

import com.xuexiang.xipc.annotation.ClassId;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.SerializeUtils;
import com.xuexiang.xipc.util.TypeUtils;

/**
 * 参数的包装器
 *
 * @author xuexiang
 * @since 2018/9/14 下午6:14
 */
public class ParameterWrapper extends BaseWrapper implements Parcelable {

    /**
     * 参数的数据
     */
    private String mData;

    //only used here.
    /**
     * 参数的类型
     */
    private Class<?> mClass;

    public static final Creator<ParameterWrapper> CREATOR
            = new Creator<ParameterWrapper>() {
        public ParameterWrapper createFromParcel(Parcel in) {
            ParameterWrapper parameterWrapper = new ParameterWrapper();
            parameterWrapper.readFromParcel(in);
            return parameterWrapper;
        }

        public ParameterWrapper[] newArray(int size) {
            return new ParameterWrapper[size];
        }
    };

    private ParameterWrapper() {

    }

    /**
     * 包装参数
     *
     * @param clazz
     * @param object
     * @throws IPCException
     */
    public ParameterWrapper(Class<?> clazz, Object object) throws IPCException {
        mClass = clazz;
        setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
        mData = SerializeUtils.encode(object);
    }

    /**
     * 包装参数
     *
     * @param object
     * @throws IPCException
     */
    public ParameterWrapper(Object object) throws IPCException {
        if (object == null) {
            setName(false, "");
            mData = null;
            mClass = null;
        } else {
            Class<?> clazz = object.getClass();
            mClass = clazz;
            setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
            mData = SerializeUtils.encode(object);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(mData);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mData = in.readString();
    }

    public String getData() {
        return mData;
    }

    public boolean isNull() {
        return mData == null;
    }

    public Class<?> getClassType() {
        return mClass;
    }
}
