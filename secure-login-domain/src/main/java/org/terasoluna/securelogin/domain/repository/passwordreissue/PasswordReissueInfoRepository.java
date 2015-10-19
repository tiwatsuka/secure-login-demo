package org.terasoluna.securelogin.domain.repository.passwordreissue;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.PasswordReissueInfo;


public interface PasswordReissueInfoRepository {

	int insert(PasswordReissueInfo info);

	PasswordReissueInfo findOne(@Param("token") String token);

	int delete(@Param("token") String token);

	int deleteExpired(@Param("date") DateTime date);
}
