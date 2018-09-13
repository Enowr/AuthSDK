package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;

/**
 * 描述: 支付宝相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public abstract class BaseAuthBuildForZFB extends BaseAuthBuild {
    boolean isShowLoading = true;
    String mOrderInfo;
    String mUri;

    BaseAuthBuildForZFB(Context context) {
        super(context, Auth.WITH_ZFB);
    }

    @Override
    public BaseAuthBuildForZFB setAction(@Auth.ActionZFB int action) {
        mAction = action;
        return this;
    }

    public BaseAuthBuildForZFB payOrderInfo(String orderInfo) {
        mOrderInfo = orderInfo;
        return this;
    }

    /**
     * 默认为显示
     */
    public BaseAuthBuildForZFB payIsShowLoading(boolean isShow) {
        isShowLoading = isShow;
        return this;
    }

    public BaseAuthBuildForZFB rouseWeb(String uri) {
        mUri = uri;
        return this;
    }

    abstract void pay(Activity activity);
}