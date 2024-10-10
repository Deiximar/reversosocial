package com.reversosocial.models.dto;

import org.springframework.web.multipart.MultipartFile;

import com.reversosocial.config.exception.validation.anotation.ValidPdf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployDto {
    private Integer id;
    @NotNull(message = "El campo puesto es requerido.")
    @NotEmpty(message = "El campo puesto es requerido.")
    @NotBlank(message = "El campo puesto es requerido.")
    private String position;

    @NotNull(message = "El campo curriculum es requerido. Por favor subir un archivo")
    @ValidPdf
    private MultipartFile curriculum;

    @NotNull(message = "El campo descripción es requerido.")
    @NotEmpty(message = "El campo descripción es requerido.")
    @NotBlank(message = "El campo descripción es requerido.")
    private String description;

    @NotNull(message = "El campo sector es requerido.")
    @NotEmpty(message = "El campo sector es requerido.")
    @NotBlank(message = "El campo sector es requerido.")
    private String sector;

    private String creatorEmail;

    private String curriculumUrl;

}
