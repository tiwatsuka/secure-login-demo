package org.terasoluna.securelogin.app.common.validation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.terasoluna.securelogin.domain.model.Account;
import org.terasoluna.securelogin.domain.model.PasswordHistory;
import org.terasoluna.securelogin.domain.model.Role;
import org.terasoluna.securelogin.domain.service.account.AccountSharedService;
import org.terasoluna.securelogin.domain.service.passwordhistory.PasswordHistorySharedService;

public class NotReusedValidator implements
		ConstraintValidator<NotReused, Object> {

	@Inject
	AccountSharedService accountSharedService;

	@Inject
	PasswordHistorySharedService passwordHistorySharedService;

	@Inject
	PasswordEncoder passwordEncoder;

	@Resource(name = "encodedPasswordHistoryValidator")
	PasswordValidator encodedPasswordHistoryValidator;

	@Value("${security.passwordHistoricalCheckingCount}")
	int passwordHistoricalCheckingCount;

	@Value("${security.passwordHistoricalCheckingPeriod}")
	int passwordHistoricalCheckingPeriod;

	private String usernamePropertyName;

	private String newPasswordPropertyName;

	private String message;

	@Override
	public void initialize(NotReused constraintAnnotation) {
		usernamePropertyName = constraintAnnotation.idPropertyName();
		newPasswordPropertyName = constraintAnnotation.newPasswordPropertyName();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		String username = (String) beanWrapper.getPropertyValue(usernamePropertyName);
		String newPassword = (String) beanWrapper
				.getPropertyValue(newPasswordPropertyName);

		Account account = accountSharedService.findOne(username);
		String currentPassword = account.getPassword();

		context.disableDefaultConstraintViolation();
		boolean result = checkNewPasswordDifferentFromCurrentPassword(
				newPassword, currentPassword, context);
		if (result && account.getRoles().contains(Role.ADMN)) {
			result = checkHistoricalPassword(username, newPassword, context);
		}

		return result;
	}

	private boolean checkNewPasswordDifferentFromCurrentPassword(
			String newPassword, String currentPassword,
			ConstraintValidatorContext context) {
		if (!passwordEncoder.matches(newPassword, currentPassword)) {
			return true;
		} else {
			context.buildConstraintViolationWithTemplate(message)
					.addPropertyNode(newPasswordPropertyName).addConstraintViolation();
			return false;
		}
	}

	private boolean checkHistoricalPassword(String username,
			String newPassword, ConstraintValidatorContext context) {
		LocalDateTime useFrom = LocalDateTime.now().minusMinutes(
				passwordHistoricalCheckingPeriod);
		List<PasswordHistory> historyByTime = passwordHistorySharedService
				.findHistoriesByUseFrom(username, useFrom);
		List<PasswordHistory> historyByCount = passwordHistorySharedService
				.findLatestHistories(username, passwordHistoricalCheckingCount);
		List<PasswordHistory> history = historyByCount.size() > historyByTime
				.size() ? historyByCount : historyByTime;

		List<PasswordData.Reference> historyData = new ArrayList<>();
		for (PasswordHistory h : history) {
			historyData.add(new PasswordData.HistoricalReference(h
					.getPassword()));
		}

		PasswordData passwordData = PasswordData.newInstance(newPassword,
				username, historyData);
		RuleResult result = encodedPasswordHistoryValidator
				.validate(passwordData);

		if (result.isValid()) {
			return true;
		} else {
			context.buildConstraintViolationWithTemplate(
					encodedPasswordHistoryValidator.getMessages(result).get(0))
					.addPropertyNode(newPasswordPropertyName).addConstraintViolation();
			return false;
		}
	}
}
