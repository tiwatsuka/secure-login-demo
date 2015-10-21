package org.terasoluna.securelogin.domain.service.passwordreissue;

import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.PasswordReissueInfo;

public interface PasswordReissueService {

	PasswordReissueInfo createReissueInfo(String username);

	boolean saveAndSendReissueInfo(PasswordReissueInfo info);

	PasswordReissueInfo findOne(String username, String token);

	boolean resetPassowrd(String username, String token, String secret,
			String rawPassword);

	boolean removeExpired(DateTime date);
}
