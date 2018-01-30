package com.gabizou.residency.api.region;

public class RegionException extends RuntimeException {

    public RegionException() {
    }

    public RegionException(String message) {
        super(message);
    }

    public RegionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegionException(Throwable cause) {
        super(cause);
    }
}
