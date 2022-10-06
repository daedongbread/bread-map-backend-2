package com.depromeet.breadmapbackend.domain.product.exception;

public class ProductAlreadyException extends RuntimeException {
    public ProductAlreadyException() { super("Product is already exist."); }
}
