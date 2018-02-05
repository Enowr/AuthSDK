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
public class AuthBuildForYL extends Auth.Builder {
    private String mOrderInfo;
    private boolean mTest = false;

    AuthBuildForYL(Context context) {
        super(context);
    }

    @Override           // 初始化资源
    void init() {
    }

    @Override           // 清理资源
    void destroy() {
        super.destroy();
    }

    @Override
    public AuthBuildForYL setAction(@Auth.ActionYL int action) {
        mAction = action;
        return this;
    }

    /**
     * 订单信息为交易流水号，即TN
     */
    public AuthBuildForYL payOrderInfo(String orderInfo) {
        mOrderInfo = orderInfo;
        return this;
    }

    /**
     * 是否是测试环境, 默认false; true: 银联测试环境，该环境中不发生真实交易; false: 银联正式环境
     */
    public AuthBuildForYL payIsTest(boolean test) {
        mTest = test;
        return this;
    }

    @Override
    public void build(AuthCallback callback) {
        super.build(callback);
        switch (mAction) {
            case Auth.Pay:
                Intent intent = new Intent(mContext, AuthActivity.class);
                intent.putExtra("Sign", Sign);
                mContext.startActivity(intent);
                break;
            default:
                mCallback.onFailed("银联暂未支持的 Action, 或未定义的 Action");
                destroy();
        }
    }

    void pay(Activity activity) {
        if (TextUtils.isEmpty(mOrderInfo)) {
            mCallback.onFailed("必须添加 OrderInfo, 使用 payOrderInfo(info) ");
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
            }
        }
    }
}