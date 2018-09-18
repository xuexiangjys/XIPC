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

import android.view.View;
import android.widget.ProgressBar;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xipc.XIPC;
import com.xuexiang.xipcdemo.R;
import com.xuexiang.xipcdemo.service.IComputeService;
import com.xuexiang.xipcdemo.service.IFileUtils;
import com.xuexiang.xipcdemo.service.ILoadingTask;
import com.xuexiang.xipcdemo.service.IUserManager;
import com.xuexiang.xipcdemo.service.LoadingCallback;
import com.xuexiang.xipcdemo.service.impl.UserManager;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/9/18 上午10:11
 */
@Page(name = "不同进程间的通信")
public class DifferentProcessFragment extends XPageFragment {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void initArgs() {
        super.initArgs();
        XIPC.connect(getContext());
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
                ToastUtils.toast("不使用XIPC获取单例的内容:" + UserManager.getInstance().getUser() + "\r\n" +
                        "使用XIPC获取单例的内容:" + userManager.getUser());
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
