package org.terasoluna.securelogin.domain.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserImage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String username;
	
	private byte[] body;
	
	private String extension;
}
