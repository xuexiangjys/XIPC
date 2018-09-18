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
import com.xuexiang.xipc.util.TypeUtils;

/**
 * 类型的包装器（用于方法的包装）
 *
 * @author xuexiang
 * @since 2018/9/14 下午4:18
 */
public class TypeWrapper extends BaseWrapper implements Parcelable {

    public static final Creator<TypeWrapper> CREATOR
            = new Creator<TypeWrapper>() {
        public TypeWrapper createFromParcel(Parcel in) {
            TypeWrapper typeWrapper = new TypeWrapper();
            typeWrapper.readFromParcel(in);
            return typeWrapper;
        }
        public TypeWrapper[] newArray(int size) {
            return new TypeWrapper[size];
        }
    };

    private TypeWrapper() {

    }

    /**
     * 包装类型
     * @param clazz
     */
    public TypeWrapper(Class<?> clazz) {
        setName(!clazz.isAnnotationPresent(ClassName.class), TypeUtils.getClassId(clazz));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
    }

}
