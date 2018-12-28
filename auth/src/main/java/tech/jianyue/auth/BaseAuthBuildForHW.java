package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 描述: 华为相关授权操作
 * 作者: WJ
 * 时间: 2018/4/3
 * 版本: 1.0
 */
public abstract class BaseAuthBuildForHW extends BaseAuthBuild {
    String mApplicationId = Auth.AuthBuilderInit.getInstance().getHWAppID();                        // 应用ID
    String mPublicKey = Auth.AuthBuilderInit.getInstance().getHWKey();                              // 公钥
    String mTradeType = "toSign";                                                                   // 交易类型（目前只在签约时使用）
    String mMerchantId = Auth.AuthBuilderInit.getInstance().getHWMerchantID();                      // 商户ID
    String mMerchantName;                                                                           // 商户名称，必填，不参与签名。开发者注册的公司名称
    String mAmount;                                                                                 // 支付金额
    String mProductName;                                                                            // 商品名称
    String mProductDescription;                                                                     // 商品描述
    String mRequestId;                                                                              // 商户订单号：开发者在支付前生成，用来唯一标识一次支付请求
    String mCountry;                                                                                // 国家码
    String mCurrency;                                                                               // 币种
    int mChannel;                                                                                   // 渠道号
    String mVersion;                                                                                // 回调接口版本号
    String mServiceCatalog;                                                                         // 分类，必填，不参与签名。该字段会影响风控策略; 应用设置为"X5"，游戏设置为"X6"
    String mExtReserved;                                                                            // 商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调CP服务端
    String mSignHW;                                                                                 // 签名, 非单机应用一定要在服务器端储存签名私钥，并在服务器端进行签名操作
    String mUrl;                                                                                    // URL

    BaseAuthBuildForHW(Context context) {
        super(context, Auth.WITH_HW);
    }

    abstract public void initHW(Activity activity);

    abstract BaseAuthBuildForHW.Controller getController(Activity activity);

    @Override
    public BaseAuthBuildForHW setAction(@Auth.ActionHW int action) {
        mAction = action;
        return this;
    }

    public BaseAuthBuildForHW payApplicationId(String appId) {
        if (TextUtils.isEmpty(appId)) {
            mApplicationId = Auth.AuthBuilderInit.getInstance().getHWAppID();
        } else {
            mApplicationId = appId;
        }
        return this;
    }

    public BaseAuthBuildForHW payPublicKey(String key) {
        if (TextUtils.isEmpty(key)) {
            mPublicKey = Auth.AuthBuilderInit.getInstance().getHWKey();
        } else {
            mPublicKey = key;
        }
        return this;
    }

    public BaseAuthBuildForHW payTradeType(String tradeType) {
        mTradeType = tradeType;
        return this;
    }

    public BaseAuthBuildForHW payMerchantId(String merchantId) {
        if (TextUtils.isEmpty(merchantId)) {
            mMerchantId = Auth.AuthBuilderInit.getInstance().getHWMerchantID();
        } else {
            mMerchantId = merchantId;
        }
        return this;
    }

    public BaseAuthBuildForHW payMerchantName(String merchantName) {
        mMerchantName = merchantName;
        return this;
    }

    public BaseAuthBuildForHW payProductName(String name) {
        mProductName = name;
        return this;
    }

    public BaseAuthBuildForHW payProductDesc(String description) {
        mProductDescription = description;
        return this;
    }

    public BaseAuthBuildForHW payAmount(String amount) {
        mAmount = amount;
        return this;
    }

    public BaseAuthBuildForHW payRequestId(String id) {
        mRequestId = id;
        return this;
    }

    public BaseAuthBuildForHW payCountry(String country) {
        mCountry = country;
        return this;
    }

    public BaseAuthBuildForHW payCurrency(String currency) {
        mCurrency = currency;
        return this;
    }

    public BaseAuthBuildForHW payChannel(int channel) {
        mChannel = channel;
        return this;
    }

    public BaseAuthBuildForHW payVersion(String version) {
        mVersion = version;
        return this;
    }

    public BaseAuthBuildForHW payServiceCatalog(String serviceCatalog) {
        mServiceCatalog = serviceCatalog;
        return this;
    }

    public BaseAuthBuildForHW payExtReserved(String extReserved) {
        mExtReserved = extReserved;
        return this;
    }

    public BaseAuthBuildForHW paySign(String sign) {
        mSignHW = sign;
        return this;
    }

    public BaseAuthBuildForHW payUrl(String url) {
        mUrl = url;
        return this;
    }

    interface Controller {
        void destroy();

        void callback(int requestCode, int resultCode, Intent data);
    }
}