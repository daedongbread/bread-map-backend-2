package com.depromeet.breadmapbackend.global.exception;

import lombok.Getter;

@Getter
public class DaedongException extends RuntimeException {
	private final DaedongStatus daedongStatus;
	private final String[] args;

	public DaedongException(final DaedongStatus daedongStatus, final String... args) {
		this.daedongStatus = daedongStatus;
		this.args = args;
	}

	public DaedongException(final DaedongStatus daedongStatus) {
		this.daedongStatus = daedongStatus;
		this.args = null;
	}
}
