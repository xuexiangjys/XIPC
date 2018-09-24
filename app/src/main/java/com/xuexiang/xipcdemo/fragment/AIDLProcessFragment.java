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

package com.xuexiang.xipcdemo.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.xuexiang.aidl.AIDLService;
import com.xuexiang.aidl.ICompute;
import com.xuexiang.xipcdemo.R;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/9/24 下午1:39
 */
@Page(name = "原生AIDL进程间通信")
public class AIDLProcessFragment extends XPageFragment {

    private ICompute mICompute;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_aidl_test;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        getContext().bindService(new Intent(getContext(), AIDLService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {

    }

    @OnClick(R.id.btn_compute)
    public void onViewClicked() {
        try {
            ToastUtils.toast("3*4=" + mICompute.calculate(3 , "*", 4));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        if (mServiceConnection != null) {
            getContext().unbindService(mServiceConnection);
        }
        super.onDestroyView();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mICompute = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mICompute = ICompute.Stub.asInterface(service);
        }
    };
}
