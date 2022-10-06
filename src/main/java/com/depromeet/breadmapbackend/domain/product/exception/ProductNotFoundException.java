package com.depromeet.breadmapbackend.domain.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() { super("Product is not existed."); }
}
