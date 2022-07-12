package com.depromeet.breadmapbackend.domain.user.exception;

public class BlockNotFoundException extends RuntimeException {
    public BlockNotFoundException() { super("Block is not existed."); }
}
