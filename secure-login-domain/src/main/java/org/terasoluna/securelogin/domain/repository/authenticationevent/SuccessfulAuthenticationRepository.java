package org.terasoluna.securelogin.domain.repository.authenticationevent;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.terasoluna.securelogin.domain.model.SuccessfulAuthentication;

public interface SuccessfulAuthenticationRepository {

	int insert(SuccessfulAuthentication accountAuthenticationLog);

	List<SuccessfulAuthentication> findLatestEvents(
			@Param("username") String username, @Param("count") long count);
}
