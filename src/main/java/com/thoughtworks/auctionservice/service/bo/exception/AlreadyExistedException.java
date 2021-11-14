package com.thoughtworks.auctionservice.service.bo.exception;

public class AlreadyExistedException extends RuntimeException {
    public AlreadyExistedException(String message) {
        super(message);
    }
}
