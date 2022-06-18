package com.depromeet.breadmapbackend.domain.flag.exception;

public class FlagBakeryAlreadyException extends RuntimeException {
        public FlagBakeryAlreadyException() { super("Bakery has already been registered on this list."); }
}
