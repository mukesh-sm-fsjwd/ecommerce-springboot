package me.smmukesh.ecommerceproject.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class APIException extends RuntimeException {

    /**
        Prevents {@code InvalidClassException} when class structure changes.
     */
    private static final long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }
}
