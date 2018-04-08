package tech.jianyue.auth;

import android.content.Context;

/**
 * 描述: Build 工厂类
 * 作者: WJ
 * 时间: 2018/4/3
 * 版本: 1.0
 */
public abstract class AuthBuildFactory {
    public AbsAuthBuildForHW getBuildByHW(Context context) {
        return null;
    }

    public AbsAuthBuildForQQ getBuildByQQ(Context context) {
        return null;
    }

    public AbsAuthBuildForWB getBuildByWB(Context context) {
        return null;
    }

    public AbsAuthBuildForWX getBuildByWX(Context context) {
        return null;
    }

    public AbsAuthBuildForYL getBuildByYL(Context context) {
        return null;
    }

    public AbsAuthBuildForZFB getBuildByZFB(Context context) {
        return null;
    }
}