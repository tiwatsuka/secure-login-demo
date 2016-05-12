package org.terasoluna.securelogin.app.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileExtensionValidator implements
		ConstraintValidator<FileExtension, MultipartFile> {

	private String[] extensions;

	private boolean ignoreCase;

	@Override
	public void initialize(FileExtension constraintAnnotation) {
		this.extensions = constraintAnnotation.extensions();
		this.ignoreCase = constraintAnnotation.ignoreCase();
	}

	@Override
	public boolean isValid(MultipartFile value,
			ConstraintValidatorContext context) {
		if (value == null)
			return true;

		String fileNameExtension = StringUtils.getFilenameExtension(value
				.getOriginalFilename());
		if (!StringUtils.hasLength(fileNameExtension))
			return false;

		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (fileNameExtension.equals(extension) || 
					ignoreCase && fileNameExtension.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

}