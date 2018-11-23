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
    private BaseAuthBuildForQQ.Controller mControllerQQ;                             // QQ 管理器
    private BaseAuthBuildForWB.Controller mControllerWB;                             // 微博管理器
    private BaseAuthBuildForWX.Controller mControllerWX;                             // 微信管理器
    private BaseAuthBuildForYL.Controller mControllerYL;                             // 银联管理器
    private BaseAuthBuildForHW.Controller mControllerHW;                             // 华为管理器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sign = getIntent().getStringExtra("Sign");
        if (!TextUtils.isEmpty(sign)) {
            initQQ(sign);
            initWB(sign);
            initYL(sign);
            initZFB(sign);
            initHW(sign);
        }
        initWX();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (mControllerWB != null) {
            mControllerWB.callbackShare();
        }
        if (mControllerWX != null) {
            mControllerWX.callback();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mControllerQQ != null) {
            mControllerQQ.callback(requestCode, resultCode, data);
        }
        if (mControllerWB != null) {
            mControllerWB.callbackSso(requestCode, resultCode, data);
        }
        if (mControllerYL != null) {
            mControllerYL.callback(requestCode, resultCode, data);
        }
        if (mControllerHW != null) {
            mControllerHW.callback(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mControllerQQ != null) {
            mControllerQQ.destroy();
            mControllerQQ = null;
        }
        if (mControllerWB != null) {
            mControllerWB.destroy();
            mControllerWB = null;
        }
        if (mControllerWX != null) {
            mControllerWX.destroy();
            mControllerWX = null;
        }
        if (mControllerYL != null) {
            mControllerYL.destroy();
            mControllerYL = null;
        }
        if (mControllerHW != null) {
            mControllerHW.destroy();
            mControllerHW = null;
        }
    }

    // 华为相关
    private void initHW(String sign) {
        BaseAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof BaseAuthBuildForHW) {
            mControllerHW = ((BaseAuthBuildForHW) builder).getController(this);
        }
    }

    // QQ 相关
    private void initQQ(String sign) {
        BaseAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof BaseAuthBuildForQQ) {
            mControllerQQ = ((BaseAuthBuildForQQ) builder).getController(this);
        }
    }

    // 微博相关
    private void initWB(String sign) {
        BaseAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof BaseAuthBuildForWB) {
            mControllerWB = ((BaseAuthBuildForWB) builder).getController(this);
        }
    }

    // 微信相关
    private void initWX() {
        for (BaseAuthBuild builder : Auth.mBuilderSet) {
            if (builder != null && builder instanceof BaseAuthBuildForWX) {
                mControllerWX = ((BaseAuthBuildForWX) builder).getController(this);
                mControllerWX.callback();
                break;
            }
        }
    }

    // 银联相关
    private void initYL(String sign) {
        BaseAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof BaseAuthBuildForYL && builder.mAction == Auth.Pay) {
            mControllerYL = ((BaseAuthBuildForYL) builder).getController(this);
            mControllerYL.pay();
        }
    }

    // 支付宝相关
    private void initZFB(String sign) {
        BaseAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof BaseAuthBuildForZFB && builder.mAction == Auth.Pay) {
            ((BaseAuthBuildForZFB) builder).pay(this);
        }
    }
}
