package org.terasoluna.securelogin.domain.repository.passwordhistory;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.PasswordHistory;

public interface PasswordHistoryRepository {
	int insert(PasswordHistory history);

	List<PasswordHistory> findByUseFrom(@Param("username") String username,
			@Param("useFrom") DateTime useFrom);

	List<PasswordHistory> findLatestHistories(
			@Param("username") String username, @Param("limit") int limit);
}
