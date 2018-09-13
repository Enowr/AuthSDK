package tech.jianyue.auth;

import android.content.Context;

/**
 * 描述:
 * 作者: WJ
 * 时间: 2018/4/4
 * 版本: 1.0
 */
public abstract class BaseAuthBuild {
    int mAction = Auth.UNKNOWN_TYPE;                            // 事件
    int mWith;                                                  // 第三方标记
    String mSign;                                               // 任务标记
    Context mContext;                                           // 上下文
    AuthCallback mCallback;                                     // 回调函数

    BaseAuthBuild(Context context, @Auth.WithThird int with) {
        mWith = with;
        mSign = String.valueOf(System.currentTimeMillis());
        mContext = context;
        init();
    }

    abstract void init();

    void destroy() {
        Auth.removeBuilder(this);
        mContext = null;
        mCallback = null;
    }

    public abstract BaseAuthBuild setAction(int action);

    public void build(AuthCallback callback) {
        if (callback == null) {
            destroy();
            throw new NullPointerException("AuthCallback is null");
        } else if (mContext == null) {
            destroy();
            throw new NullPointerException("Context is null");
        } else if (mAction == Auth.UNKNOWN_TYPE) {
            callback.onFailed("未设置Action, 请调用 setAction(action)");
            destroy();
        } else {
            mCallback = callback;
            mCallback.setWith(mWith, mAction);
            mCallback.onStart();
            Auth.addBuilder(this);
        }
    }
}