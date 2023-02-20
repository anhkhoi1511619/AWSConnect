package com.app.awsconnect.utils;

public class PreferencesKey {

    private PreferencesKey() {
    }

    public final static class Device {
        public static final String DEVICE_ID = "DEVICE_ID";
        public static final String BUS_ID = "BUS_ID";
        public static final String CERTIFICATED_ID = "CERTIFICATED_ID";
        public static final String THING_NAME = "THING_NAME";
    }

    public final static class LecipAwsCredential {
        public static final String PREFIX =
                String.format("%s_", LecipAwsCredential.class.getSimpleName());
        public static final String EXPIRATION = PREFIX.concat("EXPIRATION");
        public static final String ACCESS_KEY_ID = PREFIX.concat("ACCESS_KEY_ID");
        public static final String SECRET_ACCESS_KEY = PREFIX.concat("SECRET_ACCESS_KEY");
        public static final String SESSION_TOKEN = PREFIX.concat("SESSION_TOKEN");
    }
}
