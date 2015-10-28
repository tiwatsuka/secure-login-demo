package org.terasoluna.securelogin.domain.service.unlock;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessages;
import org.terasoluna.securelogin.domain.common.message.MessageKeys;
import org.terasoluna.securelogin.domain.service.account.AccountSharedService;
import org.terasoluna.securelogin.domain.service.authenticationevent.AuthenticationEventSharedService;

@Transactional
@Service
public class UnlockServiceImpl implements UnlockService {

	@Inject
	AccountSharedService accountSharedService;

	@Inject
	AuthenticationEventSharedService authenticationEventSharedService;

	@Override
	public boolean unlock(String username) {
		if (!accountSharedService.isLocked(username)) {
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_UL_5001));
		}

		authenticationEventSharedService
				.deleteFailureEventByUsername(username);

		return true;
	}

}
