package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.unionpay.UPPayAssistEx;

/**
 * 导入文件: libs(UPPayAssistEx.jar \ UPPayPluginExPro.jar) assets(data.bin)  jniLibs/xxx(libentryexpro.so \ libuptsmaddon.so \ libuptsmaddonmi.so)
 * 描述: 银联相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthBuildForYL extends AbsAuthBuildForYL {
    AuthBuildForYL(Context context) {
        super(context);
    }

    public static AuthBuildFactory getFactory() {
        return new AuthBuildFactory() {
            @Override
            public AbsAuthBuildForYL getBuildByYL(Context context) {
                return new AuthBuildForYL(context);
            }
        };
    }

    @Override
    AbsAuthBuildForYL.Controller getController(Activity activity) {
        return new Controller(this, activity);
    }

    @Override           // 初始化资源
    void init() {
    }

    @Override           // 清理资源
    void destroy() {
        super.destroy();
    }

    @Override
    public void build(AuthCallback callback) {
        super.build(callback);
        switch (mAction) {
            case Auth.Pay:
                Intent intent = new Intent(mContext, AuthActivity.class);
                intent.putExtra("Sign", Sign);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            default:
                if (mAction != Auth.UNKNOWN_TYPE) {
                    mCallback.onFailed("银联暂未支持的 Action");
                }
                destroy();
        }
    }

    private void pay(Activity activity) {
        if (TextUtils.isEmpty(mOrderInfo)) {
            mCallback.onFailed("必须添加 OrderInfo, 使用 payOrderInfo(info) ");
            activity.finish();
        } else {
            int i;
            if (mTest) {                                            // 银联测试环境
                i = UPPayAssistEx.startPay(activity, null, null, mOrderInfo, "01");
            } else {                                                // 银联正式环境
                i = UPPayAssistEx.startPay(activity, null, null, mOrderInfo, "00");
            }
            if (UPPayAssistEx.PLUGIN_VALID == i) {                  // 该终端已经安装控件，并启动控件

            } else if (UPPayAssistEx.PLUGIN_NOT_FOUND == i) {       // 手机终端尚未安装支付控件，需要先安装支付控件
                mCallback.onFailed("手机终端尚未安装支付控件，需要先安装支付控件 ");
                activity.finish();
            }
        }
    }

    static class Controller implements AbsAuthBuildForYL.Controller {
        private AuthBuildForYL mBuild;
        private Activity mActivity;

        Controller(AuthBuildForYL build, Activity activity) {
            mBuild = build;
            mActivity = activity;
        }

        @Override
        public void pay() {
            mBuild.pay(mActivity);
        }

        @Override
        public void destroy() {
            mBuild.destroy();
            mBuild = null;
            mActivity = null;
        }

        @Override
        public void callback(int requestCode, int resultCode, Intent data) {
            if (data != null && data.getExtras() != null) {
                String str = data.getExtras().getString("pay_result");
                if( "success".equalsIgnoreCase(str) ){
                    mBuild.mCallback.onSuccessForPay("银联支付成功");
                }  else if ("fail".equalsIgnoreCase(str)) {
                    mBuild.mCallback.onFailed("银联支付失败");
                } else if ("cancel".equalsIgnoreCase(str)) {
                    mBuild.mCallback.onCancel();
                }
            }
            mBuild.destroy();
            mActivity.finish();
        }
    }
}