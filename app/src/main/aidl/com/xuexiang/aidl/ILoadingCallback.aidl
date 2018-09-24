// ILoadingCallback.aidl
package com.xuexiang.aidl;

// Declare any non-default types here with import statements

interface ILoadingCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void callback(int progress);
}
