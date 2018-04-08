package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 描述: 银联相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public abstract class AbsAuthBuildForYL extends AbsAuthBuild {
    String mOrderInfo;
    boolean mTest = false;

    AbsAuthBuildForYL(Context context) {
        super(context, Auth.WITH_YL);
    }

    abstract Controller getController(Activity activity);

    @Override
    public AbsAuthBuildForYL setAction(@Auth.ActionYL int action) {
        mAction = action;
        return this;
    }

    /**
     * 订单信息为交易流水号，即TN
     */
    public AbsAuthBuildForYL payOrderInfo(String orderInfo) {
        mOrderInfo = orderInfo;
        return this;
    }

    /**
     * 是否是测试环境, 默认false; true: 银联测试环境，该环境中不发生真实交易; false: 银联正式环境
     */
    public AbsAuthBuildForYL payIsTest(boolean test) {
        mTest = test;
        return this;
    }

    interface Controller {
        void pay();

        void destroy();

        void callback(int requestCode, int resultCode, Intent data);
    }
}