package org.terasoluna.securelogin.domain.service.authenticationevent;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.securelogin.domain.model.FailedAuthentication;
import org.terasoluna.securelogin.domain.model.SuccessfulAuthentication;
import org.terasoluna.securelogin.domain.repository.authenticationevent.FailedAuthenticationRepository;
import org.terasoluna.securelogin.domain.repository.authenticationevent.SuccessfulAuthenticationRepository;

@Service
@Transactional
public class AuthenticationEventSharedServiceImpl implements
		AuthenticationEventSharedService {

	@Inject
	FailedAuthenticationRepository failedAuthenticationRepository;

	@Inject
	SuccessfulAuthenticationRepository successAuthenticationRepository;

	@Transactional(readOnly = true)
	@Override
	public List<SuccessfulAuthentication> findLatestSuccessEvents(
			String username, int count) {
		return successAuthenticationRepository.findLatestEvents(username, count);
	}

	@Transactional(readOnly = true)
	@Override
	public List<FailedAuthentication> findLatestFailureEvents(
			String username, int count) {
		return failedAuthenticationRepository.findLatestEvents(username, count);
	}

	@Override
	public int insertSuccessEvent(SuccessfulAuthentication event) {
		return successAuthenticationRepository.insert(event);
	}

	@Override
	public int insertFailureEvent(FailedAuthentication event) {
		return failedAuthenticationRepository.insert(event);
	}

	@Override
	public int deleteFailureEventByUsername(String username) {
		return failedAuthenticationRepository.deleteByUsername(username);
	}

}
