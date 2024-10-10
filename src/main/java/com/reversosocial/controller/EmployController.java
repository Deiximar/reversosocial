package com.reversosocial.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.reversosocial.models.dto.EmployDto;
import com.reversosocial.service.impl.EmployServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employs")
@PreAuthorize("denyAll()")
public class EmployController {

    private final EmployServiceImpl employService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<EmployDto>> getAllEmploys() {
        return new ResponseEntity<>(employService.getAllEmploys(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<EmployDto> getEmployById(@PathVariable Integer id) {
        return new ResponseEntity<EmployDto>(employService.getEmployById(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<EmployDto> createEmployOffer(@Valid @RequestBody EmployDto employDto) {
        EmployDto createdEmploy = employService.createEmploy(employDto);
        return new ResponseEntity<EmployDto>(createdEmploy, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<EmployDto> updateEmploy(@PathVariable Integer id, @RequestBody EmployDto employDto) {
        return new ResponseEntity<EmployDto>(employService.updateEmploy(id, employDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public ResponseEntity<String> deleteEmployOffer(@PathVariable Integer id) {
        return new ResponseEntity<>(employService.deleteEmploy(id), HttpStatus.OK);
    }
}
