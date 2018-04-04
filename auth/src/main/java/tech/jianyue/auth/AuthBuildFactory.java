package tech.jianyue.auth;

import android.content.Context;

/**
 * 描述: Build 工厂类
 * 作者: WJ
 * 时间: 2018/4/3
 * 版本: 1.0
 */
public abstract class AuthBuildFactory {

    public AbsAuthBuildForHW getHWBuild(Context context) {
        return null;
    }

    public AbsAuthBuildForQQ getQQBuild(Context context) {
        return null;
    }

    public AbsAuthBuildForWB getWBBuild(Context context) {
        return null;
    }

    public AbsAuthBuildForWX getWXBuild(Context context) {
        return null;
    }
}
