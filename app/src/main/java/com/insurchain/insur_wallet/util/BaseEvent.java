package com.insurchain.insur_wallet.util;

/**
 * Created by huangshan on 18/2/6.
 * event事件分发
 */

public class BaseEvent {

    //登录成功
    public static class LoginEvent {

        public LoginEvent() {
        }
    }

    //退出登录
    public static class LogoutEvent {

        public LogoutEvent() {
        }
    }

    //修改信息
    public static class UpdateInfoEvent {

        public UpdateInfoEvent() {
        }
    }

    //重新加载
    public static class ReLoadEvent {

        public ReLoadEvent() {
        }
    }
}
