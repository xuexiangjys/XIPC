// ICompute.aidl
package com.xuexiang.aidl;

// Declare any non-default types here with import statements

interface ICompute {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    float calculate(float value1, String symbol, float value2);

}
