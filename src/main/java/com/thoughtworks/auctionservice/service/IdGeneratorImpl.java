package com.thoughtworks.auctionservice.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGeneratorImpl implements IdGenerator {
    public String next() {
        return UUID.randomUUID().toString();
    }
}
