package com.reversosocial.config.exception.validation.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.reversosocial.config.exception.validation.validator.PdfFileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PdfFileValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPdf {

  String message() default "El archivo debe ser un PDF.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
