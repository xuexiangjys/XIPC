package com.xuexiang.xipcdemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.xuexiang.xipc.XIPC;
import com.xuexiang.xipcdemo.service.LoadingCallback;
import com.xuexiang.xipcdemo.service.impl.ComputeService;
import com.xuexiang.xipcdemo.service.impl.FileUtils;
import com.xuexiang.xipcdemo.service.impl.LoadingTask;
import com.xuexiang.xipcdemo.service.impl.UserManager;
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

        XIPC.register(UserManager.class);
        XIPC.register(LoadingTask.class);
        XIPC.register(FileUtils.class);
        XIPC.register(LoadingCallback.class);
        XIPC.register(ComputeService.class);

    }
}
