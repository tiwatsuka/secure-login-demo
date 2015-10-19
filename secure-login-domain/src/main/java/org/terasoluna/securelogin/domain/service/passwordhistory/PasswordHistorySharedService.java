package org.terasoluna.securelogin.domain.service.passwordhistory;

import java.util.List;

import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.PasswordHistory;

public interface PasswordHistorySharedService {

	public int insert(PasswordHistory history);

	public List<PasswordHistory> findHistoriesByUseFrom(String username,
			DateTime useFrom);

	public List<PasswordHistory> findLatestHistories(String username, int limit);

}
