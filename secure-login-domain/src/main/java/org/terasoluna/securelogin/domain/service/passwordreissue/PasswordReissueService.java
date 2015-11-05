package org.terasoluna.securelogin.domain.service.passwordreissue;

import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.PasswordReissueInfo;

public interface PasswordReissueService {

	String createRawSecret();

	boolean saveAndSendReissueInfo(String username, String rowSecret);

	PasswordReissueInfo findOne(String username, String token);

	boolean resetPassword(String username, String token, String secret,
			String rawPassword);

	boolean removeExpired(DateTime date);
}
