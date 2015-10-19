package org.terasoluna.securelogin.domain.repository.accountauthenticationlog;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.terasoluna.securelogin.domain.model.AccountAuthenticationFailureLog;

public interface AccountAuthenticationFailureLogRepository {

	int insert(AccountAuthenticationFailureLog accountAuthenticationLog);

	List<AccountAuthenticationFailureLog> findLatestLogs(
			@Param("username") String username, @Param("nbLog") long nbLog);

	int deleteByUsername(@Param("username") String username);
}
