package org.terasoluna.securelogin.domain.service.account;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;
import org.terasoluna.securelogin.domain.common.message.MessageKeys;
import org.terasoluna.securelogin.domain.model.Account;
import org.terasoluna.securelogin.domain.model.FailedAuthentication;
import org.terasoluna.securelogin.domain.model.SuccessfulAuthentication;
import org.terasoluna.securelogin.domain.model.PasswordHistory;
import org.terasoluna.securelogin.domain.repository.account.AccountRepository;
import org.terasoluna.securelogin.domain.service.authenticationevent.AuthenticationEventSharedService;
import org.terasoluna.securelogin.domain.service.passwordhistory.PasswordHistorySharedService;

@Service
@Transactional
public class AccountSharedServiceImpl implements AccountSharedService {

	@Inject
	AuthenticationEventSharedService authenticationEventSharedService;

	@Inject
	PasswordHistorySharedService passwordHistorySharedService;

	@Inject
	AccountRepository accountRepository;

	@Inject
	PasswordEncoder passwordEncoder;

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
		List<FailedAuthentication> failureEvents = authenticationEventSharedService
				.findLatestFailureEvents(username, lockingThreshold);

		if (failureEvents.size() < lockingThreshold) {
			return false;
		}

		if (failureEvents
				.get(lockingThreshold - 1)
				.getAuthenticationTimestamp()
				.isBefore(
						LocalDateTime.now().minusSeconds(lockingDuration))) {
			return false;
		}

		/*
		 * [Optional] If you intend to treat strictly successive authentication
		 * failure, use following snippet.
		 *
		 *	List<SuccessfulAuthentication> successEvents = authenticationEventSharedService
		 *			.findLatestSuccessEvents(username, 1);
		 *	if (successEvents.isEmpty()) {
		 *		return true;
		 *	}
		 *
		 *	SuccessfulAuthentication latestSuccessEvent = successEvents.get(0);
		 *	for (FailedAuthentication failureEvent : failureEvents) {
		 *		if (latestSuccessEvent.getAuthenticationTimestamp().isAfter(
		 *				failureEvent.getAuthenticationTimestamp())) {
		 *			return false;
		 *		}
		 *	}
		 */

		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public LocalDateTime getLastLoginDate(String username) {
		List<SuccessfulAuthentication> events = authenticationEventSharedService
				.findLatestSuccessEvents(username, 1);

		if (events.isEmpty()) {
			return null;
		} else {
			return events.get(0).getAuthenticationTimestamp();
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
						LocalDateTime.now()
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

		LocalDateTime passwordChangeDate = LocalDateTime.now();

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
