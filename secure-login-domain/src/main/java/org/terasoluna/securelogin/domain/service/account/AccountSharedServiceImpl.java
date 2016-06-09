package org.terasoluna.securelogin.domain.service.account;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.terasoluna.gfw.common.date.DefaultClassicDateFactory;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;
import org.terasoluna.securelogin.domain.common.message.MessageKeys;
import org.terasoluna.securelogin.domain.model.Account;
import org.terasoluna.securelogin.domain.model.FailedAuthentication;
import org.terasoluna.securelogin.domain.model.SuccessfulAuthentication;
import org.terasoluna.securelogin.domain.model.PasswordHistory;
import org.terasoluna.securelogin.domain.model.TempFile;
import org.terasoluna.securelogin.domain.model.UserImage;
import org.terasoluna.securelogin.domain.repository.account.AccountRepository;
import org.terasoluna.securelogin.domain.repository.userimage.UserImageRepository;
import org.terasoluna.securelogin.domain.service.authenticationevent.AuthenticationEventSharedService;
import org.terasoluna.securelogin.domain.service.fileupload.FileUploadSharedService;
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
	FileUploadSharedService fileUploadSharedService;
	
	@Inject
	UserImageRepository userImageRepository;
	
	@Inject
	PasswordEncoder passwordEncoder;

	@Inject
	DefaultClassicDateFactory dateFactory;
	
	@Inject
	PasswordGenerator passwordGenerator;

	@Resource(name = "passwordGenerationRules")
	List<CharacterRule> passwordGenerationRules;

	@Value("${security.lockingDurationSeconds}")
	int lockingDurationSeconds;

	@Value("${security.lockingThreshold}")
	int lockingThreshold;

	@Value("${security.passwordLifeTimeSeconds}")
	int passwordLifeTimeSeconds;

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
	public boolean exists(String username) {
		Account account = accountRepository.findOne(username);

		if (account == null) {
			return false;
		}else{
			return true;
		}
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
						dateFactory.newTimestamp().toLocalDateTime()
								.minusSeconds(lockingDurationSeconds))) {
			return false;
		}

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
				.findLatest(username, 1);
		return passwordHistories.isEmpty();
	}

	@Transactional(readOnly = true)
	@Override
	@Cacheable("isCurrentPasswordExpired")
	public boolean isCurrentPasswordExpired(String username) {
		List<PasswordHistory> passwordHistories = passwordHistorySharedService
				.findLatest(username, 1);

		if (passwordHistories.isEmpty()) {
			return true;
		}

		if (passwordHistories
				.get(0)
				.getUseFrom()
				.isBefore(
						dateFactory.newTimestamp().toLocalDateTime()
								.minusSeconds(passwordLifeTimeSeconds))) {
			return true;
		}

		return false;
	}

	@Override
	@CacheEvict(value = { "isInitialPassword", "isCurrentPasswordExpired" }, key = "#username")
	public boolean updatePassword(String username, String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		boolean result = accountRepository.updatePassword(username, password);

		LocalDateTime passwordChangeDate = dateFactory.newTimestamp()
				.toLocalDateTime();

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

	@Override
	public String create(Account account, String imageId) {
		if(exists(account.getUsername())){
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_AC_5001));
		}
		String rawPassword = passwordGenerator.generatePassword(10, passwordGenerationRules);
		account.setPassword(passwordEncoder.encode(rawPassword));
		accountRepository.create(account);
		accountRepository.setRoles(account);
		TempFile tempFile = fileUploadSharedService.findTempFile(imageId); 
		byte[] image = tempFile.getBody();
		UserImage userImage = new UserImage();
		userImage.setUsername(account.getUsername());
		userImage.setBody(image);
		userImage.setExtension(StringUtils.getFilenameExtension(tempFile.getOriginalName()));
		userImageRepository.create(userImage);		
		fileUploadSharedService.deleteTempFile(imageId);
		return rawPassword;
	}

	@Override
	public UserImage getImage(String username) {
		return userImageRepository.findOne(username);
	}
}
