package org.terasoluna.securelogin.domain.service.accountauthenticationlog;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.terasoluna.securelogin.domain.model.AccountAuthenticationFailureLog;
import org.terasoluna.securelogin.domain.model.AccountAuthenticationSuccessLog;
import org.terasoluna.securelogin.domain.repository.accountauthenticationlog.AccountAuthenticationFailureLogRepository;
import org.terasoluna.securelogin.domain.repository.accountauthenticationlog.AccountAuthenticationSuccessLogRepository;

@Service
@Transactional
public class AccountAuthenticationLogSharedServiceImpl implements
		AccountAuthenticationLogSharedService {

	@Inject
	AccountAuthenticationFailureLogRepository failureLogRepository;

	@Inject
	AccountAuthenticationSuccessLogRepository successRepository;

	@Transactional(readOnly = true)
	@Override
	public List<AccountAuthenticationSuccessLog> findLatestSuccessLogs(
			String username, int count) {
		return successRepository.findLatestLogs(username, count);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AccountAuthenticationFailureLog> findLatestFailureLogs(
			String username, int count) {
		return failureLogRepository.findLatestLogs(username, count);
	}

	@Override
	public int insertSuccessLog(AccountAuthenticationSuccessLog log) {
		return successRepository.insert(log);
	}

	@Override
	public int insertFailureLog(AccountAuthenticationFailureLog log) {
		return failureLogRepository.insert(log);
	}

	@Override
	public int deleteFailureLogByUsername(String username) {
		return failureLogRepository.deleteByUsername(username);
	}

}
