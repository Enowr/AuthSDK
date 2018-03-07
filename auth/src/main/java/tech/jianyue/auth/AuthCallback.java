package tech.jianyue.auth;

public abstract class AuthCallback {
    protected int mWith;
    protected int mAction;

    protected void setWith(@Auth.WithThird int with, int action) {
        mWith = with;
        mAction = action;
    }

    public final int getWith() {
        return mWith;
    }

    public final int getAction() {
        return mAction;
    }

    /**
     * build 开始之前调用
     */
    public void onStart() {

    }

    public void onSuccessForLogin(UserInfoForThird info) {

    }

    public void onSuccessForPay(String result) {

    }

    public void onSuccessForShare() {

    }

    public void onCancel() {

    }

    public void onFailed(String msg) {

    }
}