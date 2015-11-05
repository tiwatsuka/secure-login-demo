package org.terasoluna.securelogin.domain.service.passwordreissue;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;
import org.terasoluna.securelogin.domain.common.message.MessageKeys;
import org.terasoluna.securelogin.domain.model.Account;
import org.terasoluna.securelogin.domain.model.PasswordReissueInfo;
import org.terasoluna.securelogin.domain.repository.passwordreissue.FailedPasswordReissueRepository;
import org.terasoluna.securelogin.domain.repository.passwordreissue.PasswordReissueInfoRepository;
import org.terasoluna.securelogin.domain.service.account.AccountSharedService;
import org.terasoluna.securelogin.domain.service.mail.MailSharedService;

@Service
@Transactional
public class PasswordReissueServiceImpl implements PasswordReissueService {

	@Inject
	PasswordReissueFailureSharedService passwordReissueFailureSharedService;

	@Inject
	MailSharedService mailSharedService;

	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;

	@Inject
	FailedPasswordReissueRepository failedPasswordReissueRepository;

	@Inject
	AccountSharedService accountSharedService;

	@Inject
	JodaTimeDateFactory dateFactory;

	@Inject
	PasswordEncoder passwordEncoder;

	@Inject
	PasswordGenerator passwordGenerator;

	@Resource(name = "passwordGenerationRules")
	List<CharacterRule> passwordGenerationRules;

	@Value("${security.tokenLifeTime}")
	int tokenLifeTime;

	@Value("${app.hostAndPort}")
	String hostAndPort;

	@Value("${app.contextPath}")
	String contextPath;

	@Value("${app.passwordReissueProtocol}")
	String protocol;

	@Override
	public String createRawSecret() {
		return passwordGenerator.generatePassword(10, passwordGenerationRules);
	}

	@Override
	public boolean saveAndSendReissueInfo(String username, String rowSecret) {
		Account account = accountSharedService.findOne(username);
		
		String token = UUID.randomUUID().toString();

		DateTime expiryDate = dateFactory.newDateTime().plusSeconds(
				tokenLifeTime);

		PasswordReissueInfo info = new PasswordReissueInfo();
		info.setUsername(username);
		info.setToken(token);
		info.setSecret(passwordEncoder.encode(rowSecret));
		info.setExpiryDate(expiryDate);
		
		int count = passwordReissueInfoRepository.insert(info);

		if (count > 0) {
			String passwordResetUrl = protocol + "://" + hostAndPort
					+ contextPath + "/reissue/resetpassword/?form&username="
					+ info.getUsername() + "&token=" + info.getToken();

			mailSharedService.send(account.getEmail(), passwordResetUrl);

			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PasswordReissueInfo findOne(String username, String token) {
		PasswordReissueInfo info = passwordReissueInfoRepository.findOne(token);

		if (info == null) {
			throw new ResourceNotFoundException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_5002, token));
		}
		if (!info.getUsername().equals(username)) {
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_5001));
		}

		if (info.getExpiryDate().isBefore(dateFactory.newDateTime())) {
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_2001));
		}

		return info;
	}

	@Override
	public boolean resetPassword(String username, String token, String secret,
			String rawPassword) {
		PasswordReissueInfo info = this.findOne(username, token);
		if (!passwordEncoder.matches(secret, info.getSecret())) {
			passwordReissueFailureSharedService.resetFailure(username, token);
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_5003));
		}
		passwordReissueInfoRepository.delete(token);
		failedPasswordReissueRepository.deleteByToken(token);

		return accountSharedService.updatePassword(username, rawPassword);

	}

	@Override
	public boolean removeExpired(DateTime date) {
		failedPasswordReissueRepository.deleteExpired(date);
		passwordReissueInfoRepository.deleteExpired(date);
		return true;
	}

}
