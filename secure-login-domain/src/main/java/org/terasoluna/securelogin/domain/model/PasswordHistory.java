package org.terasoluna.securelogin.domain.model;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class PasswordHistory {
	private String username;

	private String password;

	private DateTime useFrom;
}
