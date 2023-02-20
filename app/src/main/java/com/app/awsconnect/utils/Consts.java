package com.app.awsconnect.utils;

public final class Consts {

    public static final String DEBUG_TAG = "QUICKRIDE_PoC";

    public static final String CONFIG_ROOT_PATH = "config/ict/";

    public static final String KEY_STORE_PASSWORD_CLAIM = "6387474417";
    public static final String KEY_STORE_NAME_CLAIM = "fleetClaim.jks";

    public static final String KEY_STORE_PASSWORD_PROVISIONED = "6387474417";
    public static final String KEY_STORE_NAME_PROVISIONED = "provisioned.jks";

    public static final String UPDATE_AWS_CREDENTIAL_WORKER_TAG = "UPDATE_AWS_CREDENTIAL_WORKER";

    public static final int QR_RESULT_SHOW_TIME = 3000; //[msec]

    private Consts() {
        // 何もしない
    }
}
