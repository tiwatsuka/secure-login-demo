package org.terasoluna.securelogin.domain.repository.accountauthenticationlog;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.terasoluna.securelogin.domain.model.AccountAuthenticationSuccessLog;

public interface AccountAuthenticationSuccessLogRepository {

	int insert(AccountAuthenticationSuccessLog accountAuthenticationLog);

	List<AccountAuthenticationSuccessLog> findLatestLogs(
			@Param("username") String username, @Param("nbLog") long nbLog);
}
