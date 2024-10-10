package com.reversosocial.models.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
  private Integer id;

  @NotNull(message = "El campo título es requerido.")
  @NotBlank(message = "El campo título es requerido.")
  private String title;

  @NotNull(message = "El campo descripción es requerido.")
  @NotBlank(message = "El campo descripción es requerido.")
  private String description;

  @NotNull(message = "El campo fecha es requerido.")
  @Future(message = "La fecha no debe ser anterior a la fecha actual.")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotNull(message = "El campo hora es requerido.")
  private LocalTime time;

  @NotNull(message = "El campo modalidad es requerido.")
  @NotBlank(message = "El campo modalidad es requerido.")
  private String modality;

  @NotNull(message = "El campo locación es requerido.")
  @NotBlank(message = "El campo locación es requerido.")
  private String location;

  @NotNull(message = "El campo número de participantes es requerido.")
  private Integer maxParticipants;

  @NotNull(message = "El campo sector es requerido.")
  private String sector;

  private boolean eventFull;
  private String creatorEmail;
  private boolean userSubscribed;
}
