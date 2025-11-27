package io.github.alirostom1.smartshop.exception;

public class ProductReferenceExistsException extends RuntimeException {
    public ProductReferenceExistsException(String message) {
        super(message);
    }
}
