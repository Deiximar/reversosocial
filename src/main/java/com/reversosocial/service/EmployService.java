package com.reversosocial.service;

import java.util.List;

import com.reversosocial.models.dto.EmployDto;

public interface EmployService {
  List<EmployDto> getAllEmploys();

  EmployDto getEmployById(Integer employId);

  EmployDto createEmploy(EmployDto employDto);

  EmployDto updateEmploy(Integer employId, EmployDto employDto);

  String deleteEmploy(Integer employId);

}
