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

package com.xuexiang.remotedemo.fragment;

import android.view.View;
import android.widget.ProgressBar;

import com.xuexiang.remotedemo.R;
import com.xuexiang.remotedemo.service.IComputeService;
import com.xuexiang.remotedemo.service.IFileUtils;
import com.xuexiang.remotedemo.service.ILoadingTask;
import com.xuexiang.remotedemo.service.IUserManager;
import com.xuexiang.remotedemo.service.LoadingCallback;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xipc.XIPC;
import com.xuexiang.xipc.core.channel.IPCListener;
import com.xuexiang.xipc.core.channel.IPCService;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/9/18 上午10:03
 */
@Page(name = "跨应用间进程通信")
public class CrossApplicationProcessFragment extends XPageFragment {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void initArgs() {
        super.initArgs();
        XIPC.connectApp(getContext(), "com.xuexiang.xipcdemo");
        XIPC.setIPCListener(new IPCListener() {
            @Override
            public void onIPCConnected(Class<? extends IPCService> service) {
                ToastUtils.toast("IPC服务已绑定！");
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_ipc_test;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {

    }

    @SingleClick
    @OnClick({R.id.btn_compute, R.id.btn_download, R.id.btn_get_user, R.id.btn_util_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_compute:
                IComputeService computeService = XIPC.getService(IComputeService.class);
                ToastUtils.toast("3*4=" + computeService.calculate(3 , "*", 4));
                break;
            case R.id.btn_download:
                ILoadingTask loadingTask = XIPC.getService(ILoadingTask.class, "pic.png");
                loadingTask.start(new LoadingCallback() {
                    @Override
                    public void callback(int progress) {
                        progressBar.setProgress(progress);
                    }
                });
                break;
            case R.id.btn_get_user:

                IUserManager userManager = XIPC.getInstance(IUserManager.class);
                ToastUtils.toast(userManager.getUser());
                break;
            case R.id.btn_util_test:
                IFileUtils fileUtils = XIPC.getUtilityClass(IFileUtils.class);
                ToastUtils.toast("应用目录:" + fileUtils.getExternalCacheDir(getContext()));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        XIPC.disconnect(getContext());
        super.onDestroyView();
    }
}
