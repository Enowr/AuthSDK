package tech.jianyue.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.HashSet;

/**
 * 描述: 作为第三方程序回调 Activity
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthActivity extends Activity implements WbShareCallback, IUiListener, IWXAPIEventHandler {
    static final HashSet<Auth.Builder> mBuilderSet = new HashSet<>();

    private IWXAPI mWXApi;                                                          // 微信 Api
    private Tencent mTencent;                                                       // QQ Api
    private AuthBuildForQQ mBuildQQ;                                                // QQ Build
    private SsoHandler mSsoHandler;                                                 // 微博授权 API
    private WbShareHandler mShareHandler;                                           // 微博分享 API
    private AuthCallback mCallbackWB;                                               // 微博分享回调函数

    static void addBuilder(Auth.Builder builder) {
        mBuilderSet.add(builder);
    }

    static Auth.Builder getBuilder(String key) {
        for (Auth.Builder builder : mBuilderSet) {
            if (builder != null && !TextUtils.isEmpty(builder.Sign) && builder.Sign.equals(key)) {
                return builder;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sign = getIntent().getStringExtra("Sign");
        initWX();
        initWB(sign);
        initQQ(sign);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        callbackWX();
        callbackShareWB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackSsoWB(requestCode, resultCode, data);
        callbackQQ(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Auth.Builder builder : mBuilderSet) {
            if (builder != null) {
                builder.destroy();
            }
        }
        mBuilderSet.clear();
        mWXApi = null;
        mTencent = null;
        mBuildQQ = null;
        mSsoHandler = null;
        mShareHandler = null;
        mCallbackWB = null;
    }


    // 微博相关
    private void initWB(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            final Auth.Builder builder = getBuilder(sign);
            if (builder != null && builder instanceof AuthBuildForWB) {
                if (builder.mAction == Auth.LOGIN) {
                    mSsoHandler = new SsoHandler(this);
                    mSsoHandler.authorize(new WbAuthListener() {
                        @Override
                        public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                            ((AuthBuildForWB) builder).getInfo(oauth2AccessToken);
                            finish();
                        }

                        @Override
                        public void cancel() {
                            builder.mCallback.onCancel();
                            finish();
                        }

                        @Override
                        public void onFailure(WbConnectErrorMessage message) {
                            builder.mCallback.onFailed(message.getErrorMessage() + "; code: " + message.getErrorCode());
                            finish();
                        }
                    });
                } else {
                    mShareHandler = new WbShareHandler(this);
                    mShareHandler.registerApp();
                    mCallbackWB = builder.mCallback;
                    ((AuthBuildForWB) builder).share(this, mShareHandler);
                }
            }
        }
    }

    private void callbackSsoWB(int requestCode, int resultCode, Intent data) {  // 微博 SSO 授权回调
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void callbackShareWB() {
        if (mShareHandler != null) {
            mShareHandler.doResultIntent(getIntent(), this);
        }
    }

    @Override
    public void onWbShareSuccess() {
        if (mCallbackWB != null) {
            mCallbackWB.onSuccessForShare();
        }
        finish();
    }

    @Override
    public void onWbShareCancel() {
        if (mCallbackWB != null) {
            mCallbackWB.onCancel();
        }
        finish();
    }

    @Override
    public void onWbShareFail() {
        if (mCallbackWB != null) {
            mCallbackWB.onFailed("微博分享失败");
        }
        finish();
    }


    // QQ 相关
    private void initQQ(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            final Auth.Builder builder = getBuilder(sign);
            if (builder != null && builder instanceof AuthBuildForQQ) {
                mBuildQQ = (AuthBuildForQQ) builder;
                mTencent = ((AuthBuildForQQ) builder).getQQApi();
                if (builder.mAction == Auth.LOGIN) {
                    mTencent.login(this, "all", this);
                } else {
                    mBuildQQ.share(this);
                }
            }
        }
    }

    private void callbackQQ(int requestCode, int resultCode, Intent data) {
        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, this);
        }
    }

    @Override
    public void onComplete(Object o) {
        if (mBuildQQ != null) {
            if (mBuildQQ.mAction == Auth.LOGIN) {
                mBuildQQ.getInfo((JSONObject) o);
            } else {
                mBuildQQ.mCallback.onSuccessForShare();
            }
        }
        finish();
    }

    @Override
    public void onError(UiError uiError) {
        if (mBuildQQ != null) {
            mBuildQQ.mCallback.onFailed(uiError.errorMessage);
        }
        finish();
    }

    @Override
    public void onCancel() {
        if (mBuildQQ != null) {
            mBuildQQ.mCallback.onCancel();
        }
        finish();
    }


    // 微信相关
    private void initWX() {
        for (Auth.Builder builder : mBuilderSet) {
            if (builder != null && builder instanceof AuthBuildForWX) {
                mWXApi = ((AuthBuildForWX) builder).getWXApi();
                callbackWX();
                break;
            }
        }
    }

    private void callbackWX() {
        if (mWXApi != null) {
            mWXApi.handleIntent(getIntent(), this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp != null) {
            Auth.Builder builder = getBuilder(resp.transaction);
            if (builder != null && builder instanceof AuthBuildForWX) {
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        builder.mCallback.onCancel();
                        break;
                    case BaseResp.ErrCode.ERR_OK:
                        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                            builder.mCallback.onSuccessForPay();
                        } else if (resp instanceof SendAuth.Resp && resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {                      // 微信授权登录 resp.getType() == 1
                            ((AuthBuildForWX) builder).getInfo(((SendAuth.Resp) resp).code);
                        } else if (resp instanceof SendMessageToWX.Resp && resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {      // resp.getType() == 2
                            builder.mCallback.onSuccessForShare();
                        }
                        break;
                    default:
                        if (builder.mAction == Auth.LOGIN) {
                            builder.mCallback.onFailed(TextUtils.isEmpty(resp.errStr) ? "微信登录失败" : resp.errStr);
                        } else if (builder.mAction == Auth.Pay) {
                            builder.mCallback.onFailed(TextUtils.isEmpty(resp.errStr) ? "微信支付失败" : resp.errStr);
                        } else {
                            builder.mCallback.onFailed(TextUtils.isEmpty(resp.errStr) ? "微信分享失败" : resp.errStr);
                        }
                }
            }
        }
        finish();
    }
}