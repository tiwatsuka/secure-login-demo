package org.terasoluna.securelogin.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TempFile implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String originalName;
	
	private byte[] body;
	
	private LocalDateTime uploadedDate;
	
}
