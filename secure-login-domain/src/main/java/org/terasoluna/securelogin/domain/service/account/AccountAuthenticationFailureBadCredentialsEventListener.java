package org.terasoluna.securelogin.domain.service.account;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.terasoluna.securelogin.domain.model.FailedAuthentication;
import org.terasoluna.securelogin.domain.service.authenticationevent.AuthenticationEventSharedService;

@Component
public class AccountAuthenticationFailureBadCredentialsEventListener{

	@Inject
	AuthenticationEventSharedService authenticationEventSharedService;

	@EventListener
	public void onApplicationEvent(
			AuthenticationFailureBadCredentialsEvent event) {

		String username = (String) event.getAuthentication().getPrincipal();

		FailedAuthentication failureEvents = new FailedAuthentication();
		failureEvents.setUsername(username);
		failureEvents.setAuthenticationTimestamp(LocalDateTime.now());

		authenticationEventSharedService.insertFailureEvent(failureEvents);
	}

}
