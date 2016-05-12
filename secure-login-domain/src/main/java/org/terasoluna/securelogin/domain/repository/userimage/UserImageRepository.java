package org.terasoluna.securelogin.domain.repository.userimage;

import org.springframework.stereotype.Repository;
import org.terasoluna.securelogin.domain.model.UserImage;

@Repository
public interface UserImageRepository {

	UserImage findOne(String username);
	
	boolean create(UserImage userImage);
	
}
