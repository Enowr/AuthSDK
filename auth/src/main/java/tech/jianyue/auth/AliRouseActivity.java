package tech.jianyue.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 描述: 支付宝走免密支付，支付完成后通过 scheme 回调的 activity
 * 作者: xiongsi
 * 时间: 2018/1/29
 * 版本: v545
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
    /*
     * 签约并支付后返回的return_url格式如下：
      caixinpay://?body=%E8%B4%A2%E6%96%B0%E9%80%9A%E4%B8%80%E4%B8%AA%E6%9C%88&
      buyer_email=15510285667&buyer_id=2088902059333363&exterface=alipay.acquire.page.createandpay&
      gmt_payment=2018-01-30+11%3A20%3A57&is_success=T&
      notify_id=RqPnCoPT3K9%252Fvwbh3Ih31LDy2uM1e7YA6s9s01%252FjzIjVqCyfGAlt70IBMVulHQj08XCz&
      notify_time=2018-01-30+11%3A20%3A59&notify_type=trade_status_sync&
      out_trade_no=CQ2018013011170137382&seller_email=zhifubao%40caixinmedia.com&
      seller_id=2088301009976821&subject=%E8%B4%A2%E6%96%B0%E9%80%9A%E4%B8%80%E4%B8%AA%E6%9C%88&
      total_fee=0.02&trade_no=2018013021001004360508439746&trade_status=TRADE_SUCCESS&sign=24f2a96fb20182c895f64210311c0835&sign_type=MD5

     * 只签约后返回的return_url格式如下:
        caixinpay://?agreement_no=20184930405302750336&alipay_user_id=2088902059333363&external_sign_no=PTK18013015213848702100169702&
        invalid_time=2115-02-01+00%3A00%3A00&is_success=T&product_code=GENERAL_WITHHOLDING_P&scene=INDUSTRY%7CDIGITAL_MEDIA&
        sign_modify_time=2018-01-30+15%3A21%3A56&sign_time=2018-01-30+15%3A21%3A56&status=NORMAL&valid_time=2018-01-30+15%3A21%3A56&
        sign=76c1ecb09868d974c8649041d9e5333b&sign_type=MD5
    * */
    private void checkPayResult() {
        if (mCallback != null) {
            Intent intent = getIntent();
            String data = intent.getDataString();
            String trade_status = Utils.decodeURL(data, "trade_status=");              // 根据支付宝返回的字符串中的trade_status字段来判断支付结果
            String status_agreement = Utils.decodeURL(data, "status=");                // 如果只是签订续订协议，则根据status字段来判断协议的状态

            if ("TRADE_FINISHED".equals(trade_status) || "TRADE_SUCCESS".equals(trade_status) || "NORMAL".equals(status_agreement)) { // 支付成功或签约成功
                mCallback.onSuccessForPay("支付宝支付成功");
            } else if ("TRADE_PENDING".equals(trade_status)) {                              // 等待卖家收款
                mCallback.onSuccessForPay("正在确认支付结果...");
            } else {                                                                        // 其他都认为支付失败
                mCallback.onFailed("支付宝支付失败");
            }
            mCallback = null;
        }
        finish();                                                                           // 关闭此activity
    }
}