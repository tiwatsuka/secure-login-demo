package org.terasoluna.securelogin.domain.common.scheduled;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.terasoluna.securelogin.domain.service.passwordreissue.PasswordReissueService;

public class UnnecessaryReissueInfoCleaner {

	@Inject
	PasswordReissueService passwordReissueService;

	public void cleanup() {
		passwordReissueService.removeExpired(LocalDateTime.now());
	}

}
