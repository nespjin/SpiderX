package com.nesp.fishplugin.installer;

public class InstallException extends Exception {

    public InstallException() {
    }

    public InstallException(String message) {
        super(message);
    }

    public InstallException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstallException(Throwable cause) {
        super(cause);
    }

}
