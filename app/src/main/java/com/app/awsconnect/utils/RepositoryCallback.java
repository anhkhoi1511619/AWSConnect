package com.app.awsconnect.utils;


public interface RepositoryCallback<T> {

    enum Status {
        /**
         * Client is attempting to connect.
         */
        Connecting,
        /**
         * Client successfully connected.
         */
        Connected,
        /**
         * Connection was lost. Can be user initiated disconnect or network.
         */
        ConnectionLost,
        /**
         * Automatically reconnecting after connection loss.
         */
        Reconnecting,
        /**
         * Processing successfully completed.
         */
        Success,
        /**
         * Processing error.
         */
        Error,
        /**
         * Device is already registered.
         */
        Registered,
        /**
         * Device is not registered yet.
         */
        Unregister,
    }

    /**
     * This method is called when the connection to the server is changed.
     *
     * @param status    the current status.
     * @param data      the message payload.
     * @param throwable a throwable in the case of connection exceptions.
     */
    void onStatusChanged(Status status, T data, Throwable throwable);

}
