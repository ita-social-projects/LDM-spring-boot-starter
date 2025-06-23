package com.softserve.ldm.service;

public interface DotenvService {
    /**
     * Checks if the given secretKey matches with the actual one.
     *
     * @param secretKey key entered by user
     * @author Hrenevych Ivan
     */
    void validateSecretKey(String secretKey);
}
