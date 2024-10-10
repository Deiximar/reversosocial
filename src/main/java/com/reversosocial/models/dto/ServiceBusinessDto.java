package com.reversosocial.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBusinessDto {
    private Integer id;

    @NotNull(message = "El campo nombre de titulo es requerido.")
    @NotBlank(message = "El campo nombre de titulo es reque rido.")
    private String title;
    @NotNull(message = "El campo tipo es requerido.")
    @NotBlank(message = "El campo tipo es requerido.")
    private String type;
    @NotNull(message = "El campo descripción es requerido.")
    @NotBlank(message = "El campo descripción es requerido.")
    private String description;
    @NotNull(message = "El campo Correo Electrónico es requerido.")
    @NotBlank(message = "El campo Correo Electrónico es requerido.")
    private String email;
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,}$", message = "El número de teléfono debe ser válido si se proporciona.")
    private String phone_number;
    private String sector;
    private String creatorEmail;

}
