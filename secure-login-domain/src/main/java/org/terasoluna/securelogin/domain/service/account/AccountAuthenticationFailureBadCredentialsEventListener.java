package org.terasoluna.securelogin.domain.service.account;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.securelogin.domain.model.FailedAuthentication;
import org.terasoluna.securelogin.domain.service.authenticationevent.AuthenticationEventSharedService;

@Component
public class AccountAuthenticationFailureBadCredentialsEventListener implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	@Inject
	AuthenticationEventSharedService authenticationEventSharedService;

	@Inject
	JodaTimeDateFactory dateFactory;

	@Override
	public void onApplicationEvent(
			AuthenticationFailureBadCredentialsEvent event) {

		String username = (String) event.getAuthentication().getPrincipal();

		FailedAuthentication failureEvents = new FailedAuthentication();
		failureEvents.setUsername(username);
		failureEvents.setAuthenticationTimestamp(dateFactory.newDateTime());

		authenticationEventSharedService.insertFailureEvent(failureEvents);
	}

}
