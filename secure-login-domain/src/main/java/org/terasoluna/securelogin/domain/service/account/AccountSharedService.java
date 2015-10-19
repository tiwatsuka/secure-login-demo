package org.terasoluna.securelogin.domain.service.account;

import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.Account;

public interface AccountSharedService {
	Account findOne(String username);

	DateTime getLastLoginDate(String username);

	boolean isLocked(String username);

	public boolean isInitialPassword(String username);

	public boolean isCurrentPasswordExpired(String username);

	public boolean updatePassword(String username, String rawPassword);

	public void clearPasswordValidationCache(String username);
}
