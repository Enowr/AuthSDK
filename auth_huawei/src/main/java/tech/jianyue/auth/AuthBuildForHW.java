package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.CheckUpdateHandler;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.pay.PaySignUtil;
import com.huawei.android.hms.agent.pay.handler.PayHandler;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.HuaweiIdStatusCodes;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;
import com.huawei.hms.support.api.pay.PayResultInfo;

import org.json.JSONObject;

import static com.huawei.android.hms.agent.HMSAgent.AgentResultCode.HMSAGENT_SUCCESS;

/*
 在 allprojects->repositories 里面配置 HMSSDK 的 maven 仓库: maven {url 'http://developer.huawei.com/repo/'}

 在application节点下增加APPID
 <meta-data
    android:name="com.huawei.hms.client.appid"
    <!-- value的值“xxx”用实际申请的应用ID替换，来源于开发者联盟网站应用的服务详情。-->
    android:value="appid=xxx">
 </meta-data>

 在application节点下增加provider，UpdateProvider用于HMS-SDK引导升级HMS，提供给系统安装器读取升级文件。UpdateSdkFileProvider用于应用自升级。
 <provider
    android:name="com.huawei.hms.update.provider.UpdateProvider"
    <!--“xxx.xxx.xxx”用实际的应用包名替换-->
    android:authorities="xxx.xxx.xxx.hms.update.provider"
    android:exported="false"
    android:grantUriPermissions="true" >
 </provider>
 <provider
    android:name="com.huawei.updatesdk.fileprovider.UpdateSdkFileProvider"
    <!--“xxx.xxx.xxx”用实际的应用包名替换-->
    android:authorities="xxx.xxx.xxx.updateSdk.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
 </provider>
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
            public AbsAuthBuildForHW getBuildByHW(Context context) {
                return new AuthBuildForHW(context);
            }
        };
    }

    @Override
    public void initHW(final Activity activity) {
        HMSAgent.init(activity);
        HMSAgent.connect(activity, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                if (HMSAGENT_SUCCESS == rst) {
                    HMSAgent.checkUpdate(activity, new CheckUpdateHandler() {
                        @Override
                        public void onResult(int rst) {

                        }
                    });
                }
            }
        });
    }

    @Override
    AbsAuthBuildForHW.Controller getController(Activity activity) {
        return new AuthBuildForHW.Controller(this, activity);
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
            case Auth.LOGIN:
                Intent intent = new Intent(mContext, AuthActivity.class);
                intent.putExtra("Sign", Sign);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
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
        payReq.url = mUrl;                                      // URL

        HMSAgent.Pay.pay(payReq, new PayHandler() {
            @Override
            public void onResult(int retCode, PayResultInfo payInfo) {
                if (retCode == HMSAGENT_SUCCESS && payInfo != null) {
                    boolean checkRst = PaySignUtil.checkSign(payInfo, Auth.AuthBuilder.HWKey);
                    if (checkRst) {
                        mCallback.onSuccessForPay("华为支付成功");                // 支付成功并且验签成功，发放商品
                    } else {
                        mCallback.onSuccessForPay("华为支付成功状态待查询");       // 签名失败，需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。
                    }
                } else if (retCode == HMSAgent.AgentResultCode.ON_ACTIVITY_RESULT_ERROR
                        || retCode == PayStatusCodes.PAY_STATE_TIME_OUT
                        || retCode == PayStatusCodes.PAY_STATE_NET_ERROR) {
                    mCallback.onSuccessForPay("华为支付成功状态待查询");           // 需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。
                } else {
                    mCallback.onFailed("华为支付失败");                           // 其他错误码意义参照支付api参考
                }
                destroy();
            }
        });
    }

    static class Controller implements AbsAuthBuildForHW.Controller {
        //调用HuaweiApiAvailability.getInstance().resolveError传入的第三个参数
        //作用同startactivityforresult方法中的requestcode
        final int REQUEST_HMS_RESOLVE_ERROR = 1000;
        final int REQUEST_SIGN_IN_AUTH = 1003;

        private AuthBuildForHW mBuild;
        private Activity mActivity;

        private HuaweiApiClient mClient;

        Controller(AuthBuildForHW build, Activity activity) {
            mBuild = build;
            mActivity = activity;

            // 创建基础权限的登录参数options
            HuaweiIdSignInOptions signInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                    .requestOpenId()
                    .requestServerAuthCode()
                    .requestCountryCode()
                    .requestAccessToken()
                    .requestUid()
                    .requestUnionId()
                    .build();

            HuaweiApiClient.ConnectionCallbacks connectionCallbacks = new HuaweiApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected() {                                                         // HuaweiApiClient 连接成功回调, 在这边处理业务自己的事件
                    if (mBuild.mAction == Auth.LOGIN) {
                        PendingResult<SignInResult> signInResult = HuaweiId.HuaweiIdApi.signIn(mActivity, mClient);
                        signInResult.setResultCallback(new ResultCallback<SignInResult>() {
                            @Override
                            public void onResult(SignInResult result) {
                                if(result.isSuccess()){                                             // 登录成功
                                    SignInHuaweiId account = result.getSignInHuaweiId();            // 获取的用户帐号信息在account里，开发者根据自己的需要使用. 可以获取帐号的 openid，昵称，头像 at信息
                                    UserInfoForThird userInfoForThird =
                                            new UserInfoForThird().initForHW(
                                                    account.toString(),
                                                    account.getUid(),
                                                    account.getOpenId(),
                                                    account.getDisplayName(),
                                                    account.getAccessToken(),
                                                    account.getPhotoUrl());
                                    mBuild.mCallback.onSuccessForLogin(userInfoForThird);
                                    mBuild.destroy();
                                    mActivity.finish();
                                } else {
                                    if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_AUTH) { // 帐号已登录，需要用户授权
                                        Intent intent = result.getData();
                                        if (intent != null) {
                                            mActivity.startActivityForResult(intent, REQUEST_SIGN_IN_AUTH);
                                        } else {
                                            // 异常场景，未知原因导致的登录失败，开发者可以在这走容错处理
                                            mBuild.mCallback.onFailed("华为: 登录失败, 未知原因");
                                            mBuild.destroy();
                                            mActivity.finish();
                                        }
                                    } else if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_NETWORK_ERROR) {
                                        //网络异常，请开发者自行处理
                                        mBuild.mCallback.onFailed("华为: 登录失败, 网络异常");
                                        mBuild.destroy();
                                        mActivity.finish();
                                    } else {
                                        //其他异常
                                        mBuild.mCallback.onFailed("华为: 登录失败, 其他异常");
                                        mBuild.destroy();
                                        mActivity.finish();
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int cause) {                                      // 异常断开
                    if (!mActivity.isDestroyed() && !mActivity.isFinishing()) {
                        mClient.connect(mActivity);
                    }
                }
            };

            HuaweiApiClient.OnConnectionFailedListener connectionFailedListener = new HuaweiApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {                           // 连接失败
                    if(HuaweiApiAvailability.getInstance().isUserResolvableError(result.getErrorCode())) {
                        final int errorCode = result.getErrorCode();
                        new Handler(mActivity.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // 此方法必须在主线程调用, xxxxxx.this 为当前界面的activity
                                HuaweiApiAvailability.getInstance().resolveError(mActivity, errorCode, REQUEST_HMS_RESOLVE_ERROR);
                            }
                        });
                    } else {
                        //其他错误码请参见开发指南或者API文档
                        mBuild.mCallback.onFailed("华为: 连接客户端失败, 错误码为" + result.getErrorCode());
                        mBuild.destroy();
                        mActivity.finish();
                    }
                }
            };

            //创建华为移动服务client实例用以登录华为帐号
            //需要指定api为HuaweiId.SIGN_IN_API
            //scope为HuaweiId.HUAEWEIID_BASE_SCOPE,可以不指定，HuaweiIdSignInOptions.DEFAULT_SIGN_IN默认使用该scope
            //连接回调以及连接失败监听
            mClient = new HuaweiApiClient.Builder(mActivity)
                    .addApi(HuaweiId.SIGN_IN_API, signInOptions)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .build();

            //建议在oncreate()的时候连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            mClient.connect(mActivity);
        }

        @Override
        public void destroy() {
            //建议在onDestroy()的时候停止连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            mClient.disconnect();
            mClient = null;
            mBuild.destroy();
            mBuild = null;
            mActivity = null;
        }

        @Override
        public void callback(int requestCode, int resultCode, Intent data) {
            String EXTRA_RESULT = "intent.extra.RESULT";
            // 连接失败
            if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
                if(resultCode == Activity.RESULT_OK) {
                    int result = data.getIntExtra(EXTRA_RESULT, 0);
                    if(result == ConnectionResult.SUCCESS) {
                        // 错误成功解决
                        if (!mClient.isConnecting() && !mClient.isConnected()) {
                            mClient.connect(mActivity);
                        }
                    } else if(result == ConnectionResult.CANCELED) {
                        // 解决错误过程被用户取消
                        mBuild.mCallback.onCancel();
                        mBuild.destroy();
                        mActivity.finish();
                    } else if(result == ConnectionResult.INTERNAL_ERROR) {
                        // 发生内部错误，重试可以解决
                        // 开发者可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                        mClient.connect(mActivity);
                    } else {
                        // 未知返回码
                        mBuild.mCallback.onFailed("华为: 连接客户端失败, 错误码为" + result);
                        mBuild.destroy();
                        mActivity.finish();
                    }
                } else {
                    // 调用解决方案发生错误
                    mBuild.mCallback.onFailed("华为: 连接客户端失败, 调用解决方案发生错误");
                    mBuild.destroy();
                    mActivity.finish();
                }
            } else if (requestCode == REQUEST_SIGN_IN_AUTH) {// 当用户未登录或者未授权，调用signin接口拉起对应的页面处理完毕后会将结果返回给当前activity处理
                //当返回值是-1的时候表明用户确认授权，
                if(resultCode == Activity.RESULT_OK) {      // 用户已经授权
                    SignInResult result = HuaweiId.HuaweiIdApi.getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        // 授权成功，result.getSignInHuaweiId()获取华为帐号信息
                        // 开发者处理获取到的帐号信息
                        SignInHuaweiId account = result.getSignInHuaweiId();
                        UserInfoForThird userInfoForThird =
                                new UserInfoForThird().initForHW(
                                        account.toString(),
                                        account.getUid(),
                                        account.getOpenId(),
                                        account.getDisplayName(),
                                        account.getAccessToken(),
                                        account.getPhotoUrl());
                        mBuild.mCallback.onSuccessForLogin(userInfoForThird);
                        mBuild.destroy();
                        mActivity.finish();
                    } else {
                        // 授权失败，result.getStatus()获取错误原因
                        mBuild.mCallback.onFailed("华为: 授权失败 失败原因:" + result.getStatus().toString());
                        mBuild.destroy();
                        mActivity.finish();
                    }
                } else {
                    //当resultCode 为0的时候表明用户未授权，则开发者可以处理用户未授权事件
                    mBuild.mCallback.onFailed("华为: 用户未授权");
                    mBuild.destroy();
                    mActivity.finish();
                }
            }
        }
    }
}