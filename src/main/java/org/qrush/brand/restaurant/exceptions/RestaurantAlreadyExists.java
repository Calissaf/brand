package org.qrush.brand.restaurant.exceptions;

public class RestaurantAlreadyExists extends RuntimeException {
    public RestaurantAlreadyExists(String message) {
        super(message);
    }
}
