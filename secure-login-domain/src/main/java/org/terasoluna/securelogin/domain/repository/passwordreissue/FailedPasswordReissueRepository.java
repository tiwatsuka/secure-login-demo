package org.terasoluna.securelogin.domain.repository.passwordreissue;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import org.terasoluna.securelogin.domain.model.FailedPasswordReissue;

public interface FailedPasswordReissueRepository {

	int countByToken(@Param("token") String token);

	int insert(FailedPasswordReissue event);

	int deleteByToken(@Param("token") String token);

	int deleteExpired(@Param("date") DateTime date);
}
