package com.epam.xmcy.exception;

/**
 * Custom exception for businesses cases.
 */
public class BusinessServiceException extends IllegalArgumentException {

    private String rootCause;

    public BusinessServiceException(String message) {
        super(message);
    }

    public BusinessServiceException() {
        super();
    }

    public BusinessServiceException(String message, String rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }
}
