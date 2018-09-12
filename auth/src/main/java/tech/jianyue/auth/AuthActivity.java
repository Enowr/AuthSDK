package tech.jianyue.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashSet;

/**
 * 描述: 作为第三方程序回调 Activity
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthActivity extends Activity {
    private HashSet<AbsAuthBuild> mBuilderSet = new HashSet<>();

    private AbsAuthBuildForQQ.Controller mControllerQQ;                             // QQ 管理器
    private AbsAuthBuildForWB.Controller mControllerWB;                             // 微博管理器
    private AbsAuthBuildForWX.Controller mControllerWX;                             // 微信管理器
    private AbsAuthBuildForYL.Controller mControllerYL;                             // 银联管理器
    private AbsAuthBuildForHW.Controller mControllerHW;                             // 华为管理器

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
        for (AbsAuthBuild builder : mBuilderSet) {
            if (builder != null) {
                builder.destroy();
            }
        }

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

    // QQ 相关
    private void initQQ(String sign) {
        AbsAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof AbsAuthBuildForQQ) {
            mBuilderSet.add(builder);
            mControllerQQ = ((AbsAuthBuildForQQ) builder).getController(this);
        }
    }

    // 微博相关
    private void initWB(String sign) {
        AbsAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof AbsAuthBuildForWB) {
            mBuilderSet.add(builder);
            mControllerWB = ((AbsAuthBuildForWB) builder).getController(this);
        }
    }

    // 微信相关
    private void initWX() {
        for (AbsAuthBuild builder : Auth.BuilderMap.values()) {
            if (builder != null && builder instanceof AbsAuthBuildForWX) {
                mBuilderSet.add(builder);
                mControllerWX = ((AbsAuthBuildForWX) builder).getController(this);
                mControllerWX.callback();
                break;
            }
        }
    }

    // 银联相关
    private void initYL(String sign) {
        AbsAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof AbsAuthBuildForYL && builder.mAction == Auth.Pay) {
            mBuilderSet.add(builder);
            mControllerYL = ((AbsAuthBuildForYL) builder).getController(this);
            mControllerYL.pay();
        }
    }

    // 支付宝相关
    private void initZFB(String sign) {
        AbsAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof AbsAuthBuildForZFB && builder.mAction == Auth.Pay) {
            mBuilderSet.add(builder);
            ((AbsAuthBuildForZFB) builder).pay(this);
        }
    }

    // 华为相关
    private void initHW(String sign) {
        AbsAuthBuild builder = Auth.getBuilder(sign);
        if (builder != null && builder instanceof AbsAuthBuildForHW && builder.mAction == Auth.LOGIN) {
            mBuilderSet.add(builder);
            mControllerHW = ((AbsAuthBuildForHW) builder).getController(this);
        }
    }
}