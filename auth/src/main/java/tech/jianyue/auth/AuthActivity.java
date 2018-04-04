package tech.jianyue.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * 描述: 作为第三方程序回调 Activity
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthActivity extends Activity {
    private AbsAuthBuildForWX.Controller mControllerWX;                             // 微信管理器
    private AbsAuthBuildForWB.Controller mControllerWB;                             // 微博管理器
    private AbsAuthBuildForQQ.Controller mControllerQQ;                             // QQ 管理器


    private AuthBuildForYL mBuildYL;                                                // 银联 Build


    static AbsAuthBuild getBuilder(String key) {
        return Auth.BuilderMap.get(key);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sign = getIntent().getStringExtra("Sign");
        initQQ(sign);
        initWB(sign);
        initWX();
        initZFB(sign);
        initYL(sign);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (mControllerWX != null) {
            mControllerWX.callback();
        }
        if (mControllerWB != null) {
            mControllerWB.callbackShare();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mControllerWB != null) {
            mControllerWB.callbackSso(requestCode, resultCode, data);
        }
        if (mControllerQQ != null) {
            mControllerQQ.callback(requestCode, resultCode, data);
        }

        callbackYL(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AbsAuthBuild builder : Auth.BuilderMap.values()) {
            if (builder != null) {
                builder.destroy();
            }
        }
        Auth.BuilderMap.clear();

        if (mControllerWX != null) {
            mControllerWX.destroy();
            mControllerWX = null;
        }
        if (mControllerWB != null) {
            mControllerWB.destroy();
            mControllerWB = null;
        }
        if (mControllerQQ != null) {
            mControllerQQ.destroy();
            mControllerQQ = null;
        }
    }

    // 银联相关
    private void initYL(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            final AbsAuthBuild builder = getBuilder(sign);
            if (builder != null && builder instanceof AuthBuildForYL) {
                if (builder.mAction == Auth.Pay)
                    mBuildYL = (AuthBuildForYL) builder;
                    ((AuthBuildForYL) builder).pay(this);
            }
        }
    }

    private void callbackYL(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null && mBuildYL != null) {
            String str = data.getExtras().getString("pay_result");
            if( "success".equalsIgnoreCase(str) ){
                mBuildYL.mCallback.onSuccessForPay("银联支付成功");
            }  else if ("fail".equalsIgnoreCase(str)) {
                mBuildYL.mCallback.onFailed("银联支付失败");
            } else if ("cancel".equalsIgnoreCase(str)) {
                mBuildYL.mCallback.onCancel();
            }
        }
        finish();
    }


    // 支付宝相关
    private void initZFB(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            final AbsAuthBuild builder = getBuilder(sign);
            if (builder != null && builder instanceof AuthBuildForZFB) {
                if (builder.mAction == Auth.Pay)
                ((AuthBuildForZFB) builder).pay(this);
            }
        }
    }


    // 微博相关
    private void initWB(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            final AbsAuthBuild builder = getBuilder(sign);
            if (builder != null && builder instanceof AbsAuthBuildForWB) {
                mControllerWB = ((AbsAuthBuildForWB) builder).getController(this);
            }
        }
    }

    // QQ 相关
    private void initQQ(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            final AbsAuthBuild builder = getBuilder(sign);
            if (builder != null && builder instanceof AbsAuthBuildForQQ) {
                mControllerQQ = ((AbsAuthBuildForQQ) builder).getController(this);
            }
        }
    }

    // 微信相关
    private void initWX() {
        for (AbsAuthBuild builder : Auth.BuilderMap.values()) {
            if (builder != null && builder instanceof AbsAuthBuildForWX) {
                mControllerWX = ((AbsAuthBuildForWX) builder).getController(this);
                mControllerWX.callback();
                break;
            }
        }
    }
}