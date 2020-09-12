package edu.iis.mto.testreactor.atm;

import java.util.Objects;

public class ATMOperationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ATMOperationException that = (ATMOperationException) o;
        return errorCode == that.errorCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode);
    }

    public ATMOperationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
