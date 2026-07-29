package com.crestostudio.restaurant_pos.exception;

public class RestaurantInactiveException extends RuntimeException {
    public RestaurantInactiveException(String message) {
        super(message);
    }
}
