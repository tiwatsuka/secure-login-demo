package org.terasoluna.securelogin.domain.service.account;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;

import org.terasoluna.securelogin.domain.common.message.MessageKeys;
import org.terasoluna.securelogin.domain.model.Account;
import org.terasoluna.securelogin.domain.model.AccountAuthenticationFailureLog;
import org.terasoluna.securelogin.domain.model.AccountAuthenticationSuccessLog;
import org.terasoluna.securelogin.domain.model.PasswordHistory;
import org.terasoluna.securelogin.domain.repository.account.AccountRepository;
import org.terasoluna.securelogin.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;
import org.terasoluna.securelogin.domain.service.passwordhistory.PasswordHistorySharedService;

@Service
@Transactional
public class AccountSharedServiceImpl implements AccountSharedService {

	@Inject
	AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;

	@Inject
	PasswordHistorySharedService passwordHistorySharedService;

	@Inject
	AccountRepository accountRepository;

	@Inject
	PasswordEncoder passwordEncoder;

	@Inject
	JodaTimeDateFactory dateFactory;

	@Value("${security.lockingDuration}")
	int lockingDuration;

	@Value("${security.lockingThreshold}")
	int lockingThreshold;

	@Value("${security.passwordLifeTime}")
	int passwordLifeTime;

	@Transactional(readOnly = true)
	@Override
	public Account findOne(String username) {
		Account account = accountRepository.findOne(username);

		if (account == null) {
			throw new ResourceNotFoundException(ResultMessages.error().add(
					MessageKeys.E_SL_FA_5001, username));
		}
		return account;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean isLocked(String username) {
		List<AccountAuthenticationFailureLog> failureLogs = accountAuthenticationLogSharedService
				.findLatestFailureLogs(username, lockingThreshold);

		if (failureLogs.size() < lockingThreshold) {
			return false;
		}

		if (failureLogs
				.get(lockingThreshold-1)
				.getAuthenticationTimestamp()
				.isBefore(
						dateFactory.newDateTime().minusSeconds(
								lockingDuration))) {
			return false;
		}

		/*
		 * [Optional] If you intend to treat strictly successive authentication
		 * failure, use following snippet.
		 * 
		 * List<AccountAuthenticationSuccessLog> successLogs =
		 * accountAuthenticationLogSharedService.findLatestSuccessLogs(username,
		 * 1); if(successLogs.isEmpty()){ return true; }
		 * 
		 * AccountAuthenticationSuccessLog latestSuccessLog =
		 * successLogs.get(0); for(AccountAuthenticationFailureLog failureLog :
		 * failureLogs){
		 * if(latestSuccessLog.getAuthenticationTimestamp().isAfter
		 * (failureLog.getAuthenticationTimestamp())){ return false; } }
		 */

		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public DateTime getLastLoginDate(String username) {
		List<AccountAuthenticationSuccessLog> logs = accountAuthenticationLogSharedService
				.findLatestSuccessLogs(username, 1);

		if (logs.isEmpty()) {
			return null;
		} else {
			return logs.get(0).getAuthenticationTimestamp();
		}
	}

	@Transactional(readOnly = true)
	@Override
	@Cacheable("isInitialPassword")
	public boolean isInitialPassword(String username) {
		List<PasswordHistory> passwordHistories = passwordHistorySharedService
				.findLatestHistories(username, 1);
		return passwordHistories.isEmpty();
	}

	@Transactional(readOnly = true)
	@Override
	@Cacheable("isCurrentPasswordExpired")
	public boolean isCurrentPasswordExpired(String username) {
		List<PasswordHistory> passwordHistories = passwordHistorySharedService
				.findLatestHistories(username, 1);

		if (passwordHistories.isEmpty()) {
			return true;
		}

		if (passwordHistories
				.get(0)
				.getUseFrom()
				.isBefore(
						dateFactory.newDateTime()
								.minusSeconds(passwordLifeTime))) {
			return true;
		}

		return false;
	}

	@Override
	@CacheEvict(value = { "isInitialPassword", "isCurrentPasswordExpired" }, key = "#username")
	public boolean updatePassword(String username, String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		boolean result = accountRepository.updatePassword(username, password);

		DateTime passwordChangeDate = dateFactory.newDateTime();

		PasswordHistory passwordHistory = new PasswordHistory();
		passwordHistory.setUsername(username);
		passwordHistory.setPassword(password);
		passwordHistory.setUseFrom(passwordChangeDate);
		passwordHistorySharedService.insert(passwordHistory);

		return result;
	}

	@Override
	@CacheEvict(value = { "isInitialPassword", "isCurrentPasswordExpired" }, key = "#username")
	public void clearPasswordValidationCache(String username) {
	}
}
