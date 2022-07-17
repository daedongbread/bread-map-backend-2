package com.depromeet.breadmapbackend.domain.user.exception;

public class BlockAlreadyException extends RuntimeException {
    public BlockAlreadyException() { super("Already block."); }
}
