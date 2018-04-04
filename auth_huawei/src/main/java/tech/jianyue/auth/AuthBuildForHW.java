package tech.jianyue.auth;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.pay.PaySignUtil;
import com.huawei.android.hms.agent.pay.handler.PayHandler;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.PayResultInfo;

/**
 * 在 allprojects->repositories 里面配置 HMSSDK 的 maven 仓库: maven {url 'http://developer.huawei.com/repo/'}
 * <p>
 * 在application节点下增加APPID
 * <meta-data
 * android:name="com.huawei.hms.client.appid"
 * <!-- value的值“xxx”用实际申请的应用ID替换，来源于开发者联盟网站应用的服务详情。-->
 * android:value="appid=xxx">
 * </meta-data>
 * <p>
 * 在application节点下增加provider，UpdateProvider用于HMS-SDK引导升级HMS，提供给系统安装器读取升级文件。UpdateSdkFileProvider用于应用自升级。
 * <provider
 * android:name="com.huawei.hms.update.provider.UpdateProvider"
 * <!--“xxx.xxx.xxx”用实际的应用包名替换-->
 * android:authorities="xxx.xxx.xxx.hms.update.provider"
 * android:exported="false"
 * android:grantUriPermissions="true" >
 * </provider>
 * <provider
 * android:name="com.huawei.updatesdk.fileprovider.UpdateSdkFileProvider"
 * <!--“xxx.xxx.xxx”用实际的应用包名替换-->
 * android:authorities="xxx.xxx.xxx.updateSdk.fileProvider"
 * android:exported="false"
 * android:grantUriPermissions="true">
 * </provider>
 */

/**
 * 描述: 华为相关功能
 * 作者: WJ
 * 时间: 2018/4/2
 * 版本: 1.0
 */
public class AuthBuildForHW extends AbsAuthBuildForHW {
    private AuthBuildForHW(Context context) {
        super(context);
    }

    public static AuthBuildFactory getFactory() {
        return new AuthBuildFactory() {
            @Override
            public AbsAuthBuildForHW getHWBuild(Context context) {
                return new AuthBuildForHW(context);
            }
        };
    }

    @Override
    void init() {
        if (TextUtils.isEmpty(Auth.AuthBuilder.HWAppID) || TextUtils.isEmpty(Auth.AuthBuilder.HWMerchantID) || TextUtils.isEmpty(Auth.AuthBuilder.HWKey)) {
            throw new IllegalArgumentException("HuaWei was no initialization");
        }
    }

    @Override
    void destroy() {
        super.destroy();
    }

    @Override
    public void build(AuthCallback callback) {
        super.build(callback);
        switch (mAction) {
            case Auth.Pay:
                pay();
                break;
            default:
                if (mAction != Auth.UNKNOWN_TYPE) {
                    mCallback.onFailed("华为暂未支持的 Action");
                }
                destroy();
                break;
        }
    }

    private void pay() {
        PayReq payReq = new PayReq();
        payReq.productName = mProductName;                      // 商品名称
        payReq.productDesc = mProductDescription;               // 商品描述
        payReq.merchantId = Auth.AuthBuilder.HWMerchantID;      // 商户ID，来源于开发者联盟的“支付ID”
        payReq.applicationID = Auth.AuthBuilder.HWAppID;        // 应用ID，来源于开发者联盟
        payReq.amount = mAmount;                                // 支付金额
        payReq.requestId = mRequestId;                          // 商户订单号：开发者在支付前生成，用来唯一标识一次支付请求
        payReq.country = mCountry;                              // 国家码
        payReq.currency = mCurrency;                            // 币种
        payReq.sdkChannel = mChannel;                           // 渠道号
        payReq.urlVer = mVersion;                               // 回调接口版本号
        payReq.merchantName = mMerchantName;                    // 商户名称，必填，不参与签名。开发者注册的公司名称
        payReq.serviceCatalog = mServiceCatalog;                // 应用设置为"X5"，游戏设置为"X6"
        payReq.extReserved = mExtReserved;                      // 商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调CP服务端
        payReq.sign = mSign;                                    // 签名

        HMSAgent.Pay.pay(payReq, new PayHandler() {
            @Override
            public void onResult(int retCode, PayResultInfo payInfo) {
                if (retCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && payInfo != null) {
                    boolean checkRst = PaySignUtil.checkSign(payInfo, Auth.AuthBuilder.HWKey);
                    if (checkRst) {
                        mCallback.onSuccessForPay("华为支付成功");        // 支付成功并且验签成功，发放商品
                    } else {
                        mCallback.onUnconfirmedForPay();                       // 签名失败，需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。
                    }
                } else if (retCode == HMSAgent.AgentResultCode.ON_ACTIVITY_RESULT_ERROR
                        || retCode == PayStatusCodes.PAY_STATE_TIME_OUT
                        || retCode == PayStatusCodes.PAY_STATE_NET_ERROR) {
                    mCallback.onUnconfirmedForPay();                           // 需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。
                } else {
                    mCallback.onFailed("华为支付失败");                    // 其他错误码意义参照支付api参考
                }
                destroy();
            }
        });
    }
}