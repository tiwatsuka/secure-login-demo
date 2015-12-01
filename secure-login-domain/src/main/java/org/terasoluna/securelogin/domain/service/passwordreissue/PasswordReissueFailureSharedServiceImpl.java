package org.terasoluna.securelogin.domain.service.passwordreissue;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.securelogin.domain.model.FailedPasswordReissue;
import org.terasoluna.securelogin.domain.repository.passwordreissue.FailedPasswordReissueRepository;
import org.terasoluna.securelogin.domain.repository.passwordreissue.PasswordReissueInfoRepository;

@Service
@Transactional
public class PasswordReissueFailureSharedServiceImpl implements
		PasswordReissueFailureSharedService {

	@Inject
	FailedPasswordReissueRepository failedPasswordReissueRepository;

	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;

	@Value("${security.tokenValidityThreshold}")
	int tokenValidityThreshold;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void resetFailure(String username, String token) {
		FailedPasswordReissue event = new FailedPasswordReissue();
		event.setToken(token);
		event.setAttemptDate(LocalDateTime.now());
		failedPasswordReissueRepository.insert(event);

		int count = failedPasswordReissueRepository
				.countByToken(token);
		if (count >= tokenValidityThreshold) {
			failedPasswordReissueRepository.deleteByToken(token);
			passwordReissueInfoRepository.delete(token);
		}
	}

}
