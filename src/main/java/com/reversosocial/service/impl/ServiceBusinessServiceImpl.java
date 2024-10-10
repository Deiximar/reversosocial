package com.reversosocial.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.reversosocial.config.exception.ResourceNotFoundException;
import com.reversosocial.config.exception.UsernameNotFoundException;
import com.reversosocial.models.dto.ServiceBusinessDto;
import com.reversosocial.models.entity.Sector;
import com.reversosocial.models.entity.ServiceBusiness;
import com.reversosocial.models.entity.User;
import com.reversosocial.repository.SectorRepository;
import com.reversosocial.repository.ServiceBusinessRepository;
import com.reversosocial.repository.UserRepository;
import com.reversosocial.service.ServiceBusinessService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceBusinessServiceImpl implements ServiceBusinessService {
    private final ServiceBusinessRepository serviceBusinessRepository;
    private final UserRepository userRepository;
    private final SectorRepository sectorRepository;
    private final ModelMapper modelMapper;

    @Override
    public ServiceBusinessDto createService(ServiceBusinessDto serviceDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        Sector sector = sectorRepository.findBySector(serviceDto.getSector())
                .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado"));
        ServiceBusiness serviceBusiness = modelMapper.map(serviceDto, ServiceBusiness.class);
        serviceBusiness.setUser(user);
        serviceBusiness.setSector(sector);
        ServiceBusiness savedService = serviceBusinessRepository.save(serviceBusiness);
        return modelMapper.map(savedService, ServiceBusinessDto.class);
    }

    @Override
    public String deleteService(Integer serviceId) {

        ServiceBusiness serviceBusiness = serviceBusinessRepository.findById(serviceId)
                .orElseThrow(() -> new UsernameNotFoundException("Servicio no encontrado."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if (!isOwnerOrAdmin(serviceBusiness, userEmail, authentication)) {
            throw new AccessDeniedException("No tienes permisos para eliminar este servicio.");
        }

        serviceBusinessRepository.delete(serviceBusiness);
        return "El servicio ha sido eliminado existosamente";
    }

    @Override
    public ServiceBusinessDto updateService(Integer serviceBusinessId, ServiceBusinessDto serviceBusinessDto) {
        ServiceBusiness serviceBusiness = serviceBusinessRepository.findById(serviceBusinessId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if (!isOwnerOrAdmin(serviceBusiness, userEmail, authentication)) {
            throw new AccessDeniedException("No tienes permiso para modificar este servicio.");
        }
        Sector sector = sectorRepository.findBySector(serviceBusinessDto.getSector())
                .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado."));
        serviceBusiness.setTitle(serviceBusinessDto.getTitle());
        serviceBusiness.setType(serviceBusinessDto.getType());
        serviceBusiness.setEmail(serviceBusinessDto.getEmail());
        serviceBusiness.setDescription(serviceBusinessDto.getDescription());
        serviceBusiness.setPhone_number(serviceBusinessDto.getPhone_number());
        serviceBusiness.setSector(sector);
        ServiceBusiness updatedService = serviceBusinessRepository.save(serviceBusiness);
        return modelMapper.map(updatedService, ServiceBusinessDto.class);
    }

    @Override
    public List<ServiceBusinessDto> getAllServices() {
        List<ServiceBusiness> serviceBusinesses = serviceBusinessRepository.findAll();
        if (serviceBusinesses.isEmpty()) {
            throw new ResourceNotFoundException("No hay servicios disponibles");
        }
        return serviceBusinesses.stream().map(this::mapServiceToDto).toList();
    }

    @Override
    public ServiceBusinessDto getServiceById(Integer serviceId) {
        ServiceBusiness serviceBusiness = serviceBusinessRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado."));
        return mapServiceToDto(serviceBusiness);
    }

    private boolean isOwnerOrAdmin(ServiceBusiness serviceBusiness, String userEmail, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_FEMSENIORADMIN"));
        return serviceBusiness.getUser().getEmail().equals(userEmail) || isAdmin;
    }

    private ServiceBusinessDto mapServiceToDto(ServiceBusiness serviceBusiness) {
        ServiceBusinessDto serviceBusinessDto = modelMapper.map(serviceBusiness, ServiceBusinessDto.class);
        serviceBusinessDto.setCreatorEmail(serviceBusiness.getUser().getEmail());
        return serviceBusinessDto;
    }
}