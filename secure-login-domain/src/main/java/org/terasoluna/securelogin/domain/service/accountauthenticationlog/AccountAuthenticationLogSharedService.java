package org.terasoluna.securelogin.domain.service.accountauthenticationlog;

import java.util.List;

import org.terasoluna.securelogin.domain.model.AccountAuthenticationFailureLog;
import org.terasoluna.securelogin.domain.model.AccountAuthenticationSuccessLog;

public interface AccountAuthenticationLogSharedService {

	List<AccountAuthenticationSuccessLog> findLatestSuccessLogs(
			String username, int count);

	List<AccountAuthenticationFailureLog> findLatestFailureLogs(
			String username, int count);

	int insertSuccessLog(AccountAuthenticationSuccessLog log);

	int insertFailureLog(AccountAuthenticationFailureLog log);

	int deleteFailureLogByUsername(String username);
}
