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

package com.xuexiang.remotedemo;

import android.app.Application;
import android.content.Context;

import com.xuexiang.remotedemo.service.IComputeService;
import com.xuexiang.remotedemo.service.IFileUtils;
import com.xuexiang.remotedemo.service.ILoadingTask;
import com.xuexiang.remotedemo.service.IUserManager;
import com.xuexiang.remotedemo.service.LoadingCallback;
import com.xuexiang.xipc.XIPC;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xutil.XUtil;

import java.util.List;

/**
 * @author xuexiang
 * @since 2018/9/14 下午2:47
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        XUtil.init(this);
        XUtil.debug(true);

        initXIPC();

        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() { //页面注册
            @Override
            public List<PageInfo> registerPages(Context context) {
                return AppPageConfig.getInstance().getPages(); //自动注册页面
            }
        }).debug("PageLog").enableWatcher(true).init(this);
    }

    private void initXIPC() {
        XIPC.init(this);
        XIPC.debug(BuildConfig.DEBUG);

        //注册包名下的所有定义的服务接口
        XIPC.register("com.xuexiang.remotedemo.service");
    }
}
