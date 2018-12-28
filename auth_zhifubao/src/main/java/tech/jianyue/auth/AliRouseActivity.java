package tech.jianyue.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 描述: 支付宝走免密支付，支付完成后通过 scheme 回调的 activity
 * 作者: xiongsi
 * 时间: 2018/1/29
 * 版本: 1.0
 */
public class AliRouseActivity extends Activity {
    static AuthCallback mCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPayResult();
    }

    /**
     * 判断是否完成支付，并发出通知
     */
    private void checkPayResult() {
        try {                                                                               // 应用程序对 Intent.getXXXExtra()获取的异常或者畸形数据处理时进行异常捕获
            if (mCallback != null) {
                Intent intent = getIntent();
                String data = intent.getDataString();
                String trade_status = Utils.decodeURL(data, "trade_status=");           // 根据支付宝返回的字符串中的trade_status字段来判断支付结果
                String status_agreement = Utils.decodeURL(data, "status=");             // 如果只是签订续订协议，则根据status字段来判断协议的状态

                if ("TRADE_FINISHED".equals(trade_status) || "TRADE_SUCCESS".equals(trade_status) || "NORMAL".equals(status_agreement)) { // 支付成功或签约成功
                    mCallback.onSuccessForRouse(trade_status, "支付宝签约支付成功");
                } else if ("TRADE_PENDING".equals(trade_status)) {                          // 等待卖家收款
                    mCallback.onSuccessForRouse(trade_status,"正在确认签约支付结果...");
                } else {                                                                    // 其他都认为支付失败
                    mCallback.onFailed(trade_status,"支付宝签约支付失败");
                }
                mCallback = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mCallback != null) {
                mCallback.onFailed(String.valueOf(Auth.ErrorUnknown), "支付宝签约支付失败");
            }
        }
        finish();                                                                           // 关闭此activity
    }
}