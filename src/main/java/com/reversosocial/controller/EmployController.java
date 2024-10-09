package com.reversosocial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reversosocial.models.dto.EmployDto;
import com.reversosocial.models.entity.Employ;
import com.reversosocial.service.EmployService;

@RestController
@RequestMapping("/api/employs")
@PreAuthorize("denyAll()")
public class EmployController {
    
    @Autowired
    private EmployService employService;

    @GetMapping
    public List<EmployDto> getAllEmploys() {
    return employService.getAllEmploys();
}

    @GetMapping("/{id}")
    public ResponseEntity<EmployDto> getEmployById(@PathVariable Long id) {
    EmployDto employ = employService.getEmployById(id);
    if (employ != null) {
        return ResponseEntity.ok(employ);
    } else {
        return ResponseEntity.notFound().build();
    }
}


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<EmployDto> createEmployOffer(
    @RequestParam("position") String position,
    @RequestParam("description") String description,
    @RequestParam("cvFile") MultipartFile cvFile,
    @RequestParam("sectorId") int sectorId) {

    EmployDto createdEmploy = employService.createEmployOffer(position, description, cvFile, sectorId);

    return ResponseEntity.ok(createdEmploy);
}
    @PutMapping("/{id}")
    public ResponseEntity<Employ> updateEmploy(@PathVariable Long id, @RequestBody Employ employDetails) {
        Employ employ = employService.updateEmployOffer(id, employDetails);
        return ResponseEntity.ok(employ);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployOffer(@PathVariable Long id) {
        employService.deleteEmployOffer(id);
        return ResponseEntity.noContent().build();
    } 
}

       

