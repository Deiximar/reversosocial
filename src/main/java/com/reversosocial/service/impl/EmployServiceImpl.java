package com.reversosocial.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.reversosocial.config.exception.ResourceNotFoundException;
import com.reversosocial.config.exception.UsernameNotFoundException;
import com.reversosocial.models.dto.EmployDto;
import com.reversosocial.models.entity.Employ;
import com.reversosocial.models.entity.Sector;
import com.reversosocial.models.entity.User;
import com.reversosocial.repository.EmployRepository;
import com.reversosocial.repository.SectorRepository;
import com.reversosocial.repository.UserRepository;
import com.reversosocial.service.EmployService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployServiceImpl implements EmployService {

    private final EmployRepository employRepository;
    private final SectorRepository sectorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployDto> getAllEmploys() {
        List<Employ> employs = employRepository.findAll();
        if (employs.isEmpty()) {
            throw new ResourceNotFoundException("No hay curriculums disponibles.");
        }
        return employs.stream().map(this::mapEmployToDto).toList();
    }

    @Override
    public EmployDto getEmployById(Integer employId) {
        Employ employ = employRepository.findById(employId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum no encontrado."));
        return mapEmployToDto(employ);
    }

    @Override
    public EmployDto createEmploy(EmployDto employDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("CREATE"));
        if (!hasPermission) {
            throw new AccessDeniedException("No tienes permiso para crear un curriculum.");
        }
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

        Sector sector = sectorRepository.findBySector(employDto.getSector())
                .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado."));

        Employ employ = mapEmployToEntity(employDto);
        employ.setUser(user);
        employ.setSector(sector);
        Employ createdEmploy = employRepository.save(employ);
        return mapEmployToDto(createdEmploy);
    }

    @Override
    public EmployDto updateEmploy(Integer employId, EmployDto employDto) {

        Employ employ = employRepository.findById(employId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum no encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if (!isOwnerOrAdmin(employ, userEmail, authentication)) {
            throw new AccessDeniedException("No tienes permiso para modificar este curriculum.");
        }
        Sector sector = sectorRepository.findBySector(employDto.getSector())
                .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado."));

        employ.setPosition(employDto.getPosition());
        employ.setCurriculum(employDto.getCurriculum());
        employ.setDescription(employDto.getDescription());
        employ.setSector(sector);

        Employ updatedemploy = employRepository.save(employ);
        return mapEmployToDto(updatedemploy);
    }

    @Override
    public String deleteEmploy(Integer employId) {
        Employ employ = employRepository.findById(employId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum no encontrado."));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        if (!isOwnerOrAdmin(employ, userEmail, authentication)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este curriculum.");
        }
        employRepository.delete(employ);
        return "El empleo ha sido eliminado exitosamente.";
    }

    private Employ mapEmployToEntity(EmployDto employDto) {
        Employ employ = modelMapper.map(employDto, Employ.class);
        return employ;
    }

    private EmployDto mapEmployToDto(Employ employ) {
        EmployDto employDto = modelMapper.map(employ, EmployDto.class);
        employDto.setCreatorEmail(employ.getUser().getEmail());
        return employDto;
    }

    private boolean isOwnerOrAdmin(Employ employ, String userEmail, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_FEMSENIORADMIN"));
        return employ.getUser().getEmail().equals(userEmail) || isAdmin;
    }
}
