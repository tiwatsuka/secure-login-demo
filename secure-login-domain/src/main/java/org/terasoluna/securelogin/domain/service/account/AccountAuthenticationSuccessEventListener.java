package org.terasoluna.securelogin.domain.service.account;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import org.terasoluna.securelogin.domain.model.AccountAuthenticationSuccessLog;
import org.terasoluna.securelogin.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;
import org.terasoluna.securelogin.domain.service.userdetails.LoggedInUser;

@Component
public class AccountAuthenticationSuccessEventListener implements
		ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountAuthenticationSuccessEventListener.class);

	@Inject
	AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;

	@Inject
	JodaTimeDateFactory dateFactory;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		logger.info("ログイン成功時の処理をここに書けます -> {}", event);

		LoggedInUser details = (LoggedInUser) event.getAuthentication()
				.getPrincipal();

		AccountAuthenticationSuccessLog successLog = new AccountAuthenticationSuccessLog();
		successLog.setUsername(details.getUsername());
		successLog.setAuthenticationTimestamp(dateFactory.newDateTime());

		accountAuthenticationLogSharedService.insertSuccessLog(successLog);
	}

}
