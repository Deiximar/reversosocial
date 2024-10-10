package com.reversosocial.config.exception.validation.validator;

import org.springframework.web.multipart.MultipartFile;

import com.reversosocial.config.exception.validation.anotation.ValidPdf;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PdfFileValidator implements ConstraintValidator<ValidPdf, MultipartFile> {

  @Override
  public void initialize(ValidPdf constraintAnnotation) {
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null || file.isEmpty()) {
      return false;
    }
    return file.getContentType() != null && file.getContentType().equals("application/pdf");
  }
}