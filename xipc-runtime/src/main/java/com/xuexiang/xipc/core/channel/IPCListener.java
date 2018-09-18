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

package com.xuexiang.xipc.core.channel;

/**
 * IPC服务监听回调
 *
 * @author xuexiang
 * @since 2018/9/17 下午6:15
 */
public abstract class IPCListener {

    public abstract void onIPCConnected(Class<? extends IPCService> service);

    public void onIPCDisconnected(Class<? extends IPCService> service) {

    }

}
