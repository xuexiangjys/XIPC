package com.xuexiang.xipcdemo.fragment;

import android.view.KeyEvent;
import android.view.View;

import com.xuexiang.xipcdemo.activity.SecondProcessActivity;
import com.xuexiang.xipcdemo.service.impl.UserManager;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageContainerListFragment;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.common.ClickUtils;

import java.util.List;

/**
 * @author xuexiang
 * @since 2018/9/14 下午2:50
 */
@Page(name = "XIPC 进程间通信框架")
public class MainFragment extends XPageSimpleListFragment {

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("同进程通信");
        lists.add("不同进程间的通信");
        lists.add("原生AIDL进程间通信");
        return lists;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        UserManager.getInstance().setUser("我的名字改了!");//修改一下单例的内容
    }

    @Override
    protected void onItemClick(int position) {
        switch(position) {
            case 0:
                openPage(SameProcessFragment.class);
                break;
            case 1:
                ActivityUtils.startActivity(SecondProcessActivity.class);
                break;
            case 2:
                openPage(AIDLProcessFragment.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected TitleBar initTitleBar() {
        return super.initTitleBar().setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickUtils.exitBy2Click();
            }
        });
    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click();
        }
        return true;
    }



}
