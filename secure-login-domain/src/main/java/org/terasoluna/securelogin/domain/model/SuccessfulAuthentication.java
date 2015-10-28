package org.terasoluna.securelogin.domain.model;

import java.io.Serializable;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class SuccessfulAuthentication implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;

	private DateTime authenticationTimestamp;

}
