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

import com.xuexiang.xipc.annotation.MethodId;
import com.xuexiang.xipc.util.TypeUtils;

import java.lang.reflect.Method;

/**
 * 方法的包装器
 *
 * @author xuexiang
 * @since 2018/9/14 下午5:47
 */
public class MethodWrapper extends BaseWrapper implements Parcelable {

    /**
     * 参数类型的集合
     */
    private TypeWrapper[] mParameterTypes;

    /**
     * 返回值的类型
     */
    private TypeWrapper mReturnType;

    public static final Creator<MethodWrapper> CREATOR
            = new Creator<MethodWrapper>() {
        public MethodWrapper createFromParcel(Parcel in) {
            MethodWrapper methodWrapper = new MethodWrapper();
            methodWrapper.readFromParcel(in);
            return methodWrapper;
        }

        public MethodWrapper[] newArray(int size) {
            return new MethodWrapper[size];
        }
    };

    private MethodWrapper() {
    }

    /**
     * 包装方法
     *
     * @param method
     */
    public MethodWrapper(Method method) {
        setName(!method.isAnnotationPresent(MethodId.class), TypeUtils.getMethodId(method));
        Class<?>[] classes = method.getParameterTypes();
        if (classes == null) {
            classes = new Class<?>[0];
        }
        int length = classes.length;
        mParameterTypes = new TypeWrapper[length];
        for (int i = 0; i < length; ++i) {
            mParameterTypes[i] = new TypeWrapper(classes[i]);
        }
        mReturnType = new TypeWrapper(method.getReturnType());
    }

    /**
     * 包装方法的方法名和参数
     *
     * @param methodName
     * @param parameterTypes
     */
    public MethodWrapper(String methodName, Class<?>[] parameterTypes) {
        setName(true, methodName);
        if (parameterTypes == null) {
            parameterTypes = new Class<?>[0];
        }
        int length = parameterTypes.length;
        mParameterTypes = new TypeWrapper[length];
        for (int i = 0; i < length; ++i) {
            mParameterTypes[i] = new TypeWrapper(parameterTypes[i]);
        }
        mReturnType = null;
    }

    /**
     * 包装方法的参数
     *
     * @param parameterTypes
     */
    public MethodWrapper(Class<?>[] parameterTypes) {
        setName(false, "");
        if (parameterTypes == null) {
            parameterTypes = new Class<?>[0];
        }
        int length = parameterTypes.length;
        mParameterTypes = new TypeWrapper[length];
        for (int i = 0; i < length; ++i) {
            mParameterTypes[i] = parameterTypes[i] == null ? null : new TypeWrapper(parameterTypes[i]);
        }
        mReturnType = null;
    }

    public TypeWrapper[] getParameterTypes() {
        return mParameterTypes;
    }

    public TypeWrapper getReturnType() {
        return mReturnType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeParcelableArray(mParameterTypes, flags);
        parcel.writeParcelable(mReturnType, flags);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        ClassLoader classLoader = MethodWrapper.class.getClassLoader();
        Parcelable[] parcelables = in.readParcelableArray(classLoader);
        if (parcelables == null) {
            mParameterTypes = null;
        } else {
            int length = parcelables.length;
            mParameterTypes = new TypeWrapper[length];
            for (int i = 0; i < length; ++i) {
                mParameterTypes[i] = (TypeWrapper) parcelables[i];
            }
        }
        mReturnType = in.readParcelable(classLoader);
    }

}
