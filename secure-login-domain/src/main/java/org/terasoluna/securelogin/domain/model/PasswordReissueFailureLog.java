package org.terasoluna.securelogin.domain.model;

import lombok.Data;

import org.joda.time.DateTime;

@Data
public class PasswordReissueFailureLog {

	private String token;

	private DateTime attemptDate;

}
