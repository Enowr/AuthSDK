package tech.jianyue.auth;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * 描述: 支付宝相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthBuildForZFB extends Auth.Builder {
    private static boolean IsShowLoading = true;
    private Activity mActivity;
    private String mOrderInfo;

    AuthBuildForZFB(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    /**
     * 获取 SDK 版本号
     */
    public static String getSDKVersion(AuthActivity activity) {
        PayTask payTask = new PayTask(activity);
        return payTask.getVersion();
    }

    @Override           // 初始化资源
    void init() {
    }

    @Override           // 清理资源
    void destroy() {
        super.destroy();
        mActivity = null;
        IsShowLoading = true;
    }

    @Override
    public AuthBuildForZFB setAction(@Auth.ActionZFB int action) {
        mAction = action;
        return this;
    }

    public AuthBuildForZFB payOrderInfo(String orderInfo) {
        mOrderInfo = orderInfo;
        return this;
    }

    /**
     * 默认为显示
     */
    public AuthBuildForZFB payIsShowLoading(boolean isShow) {
        IsShowLoading = isShow;
        return this;
    }

    @Override
    public void build(AuthCallback callback) {
        super.build(callback);
        switch (mAction) {
            case Auth.Pay:
                pay();
                break;
            default:
                mCallback.onFailed("支付宝暂未支持的 Action, 或未定义的 Action");
                destroy();
        }
    }

    private void pay() {
        if (TextUtils.isEmpty(mOrderInfo)) {
            mCallback.onFailed("必须添加 OrderInfo, 使用 payOrderInfo(info) ");
        } else {
            new AuthBuildForZFB.Pay(mActivity, mCallback).execute(mOrderInfo);
        }
        destroy();
    }

    private static class Pay extends AsyncTask<String, Void, Map<String, String>> {
        private AuthCallback callback;                                      // 回调函数
        private Activity activity;

        Pay(Activity activity, AuthCallback callback) {
            this.callback = callback;
            this.activity = activity;
        }

        @Override
        protected Map<String, String> doInBackground(String... strings) {
            try {
                PayTask pay = new PayTask(activity);
                return pay.payV2(strings[0], IsShowLoading);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> resultMap) {
            super.onPostExecute(resultMap);
            if (resultMap != null) {
                String memo = null;
                String result = null;
                String resultStatus = null;

                for (String key : resultMap.keySet()) {
                    if (TextUtils.equals(key, "resultStatus")) {
                        resultStatus = resultMap.get(key);
                    } else if (TextUtils.equals(key, "result")) {
                        result = resultMap.get(key);
                    } else if (TextUtils.equals(key, "memo")) {
                        memo = resultMap.get(key);
                    }
                }
                // 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                // 判断 resultStatus 为 9000 则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {               // 该笔订单是否真实支付成功，需要依赖服务端的异步通知
                    callback.onSuccessForPay();
                } else {                                                        // 判断resultStatus 为非“9000”则代表可能支付失败, 该笔订单真实的支付结果，需要依赖服务端的异步通知
                    if (TextUtils.equals(resultStatus, "8000")) {           // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        callback.onSuccessForPay();                             // 默认为支付成功
                    } else {                                                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        callback.onFailed("支付宝支付失败");
                    }
                }
            } else {
                callback.onFailed("支付宝支付失败");
            }
            callback = null;
            activity = null;
        }
    }
}