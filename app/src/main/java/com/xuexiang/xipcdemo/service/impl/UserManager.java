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

package com.xuexiang.xipcdemo.service.impl;

import com.xuexiang.xipc.annotation.ClassName;
import com.xuexiang.xipc.annotation.MethodName;
import com.xuexiang.xipc.annotation.Singleton;
import com.xuexiang.xipcdemo.service.IUserManager;

/**
 * 测试单例的获取
 *
 * @author xuexiang
 * @since 2018/9/18 上午9:56
 */
@ClassName("UserManager")
public class UserManager implements IUserManager {

    private static volatile UserManager sInstance = null;

    private String mUser;

    private UserManager() {
        mUser = "我的名字叫:XIPC";
    }

    @Singleton
    public static UserManager getInstance() {
        if (sInstance == null) {
            synchronized (UserManager.class) {
                if (sInstance == null) {
                    sInstance = new UserManager();
                }
            }
        }
        return sInstance;
    }

    @MethodName("getUser")
    @Override
    public String getUser() {
        return mUser;
    }

    public UserManager setUser(String user) {
        mUser = user;
        return this;
    }
}
