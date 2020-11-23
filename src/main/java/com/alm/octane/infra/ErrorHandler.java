package com.alm.octane.infra;

import org.jbehave.core.failures.UUIDExceptionWrapper;

public class ErrorHandler {

    public static void error(String msg) throws UUIDExceptionWrapper {
        error(msg, null);
    }

    public static void error(String msg, Exception e) throws UUIDExceptionWrapper {
        String errorMsg = String.format("%s%s", Constants.errorPrefix, msg);
        System.out.println(errorMsg);
        throw new UUIDExceptionWrapper(errorMsg, e);
    }
}