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

package com.xuexiang.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.xuexiang.xipcdemo.service.impl.ComputeService;

/**
 * aidl服务
 *
 * @author xuexiang
 * @since 2018/9/24 下午1:49
 */
public class AIDLService extends Service {

    private RemoteCallbackList<ILoadingCallback> mRemoteCallbackList = new RemoteCallbackList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RemoteBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    /**
     * 远程绑定服务
     */
    public class RemoteBinder extends ICompute.Stub {

        private ComputeService mComputeService;

        public RemoteBinder() {
            mComputeService = new ComputeService();
        }

        @Override
        public float calculate(float value1, String symbol, float value2) throws RemoteException {
            return mComputeService.calculate(value1, symbol, value2);
        }


    }

}
