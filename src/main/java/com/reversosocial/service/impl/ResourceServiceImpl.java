package com.reversosocial.service.impl;

import java.util.List;
import org.modelmapper.ModelMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.reversosocial.config.exception.ResourceNotFoundException;
import com.reversosocial.config.exception.UsernameNotFoundException;
import com.reversosocial.models.dto.ResourceDto;
import com.reversosocial.models.entity.Resource;
import com.reversosocial.models.entity.User;
import com.reversosocial.repository.ResourceRepository;
import com.reversosocial.repository.UserRepository;
import com.reversosocial.service.ResourceService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResourceDto createResource(ResourceDto resourceDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

        Resource resource = mapResourceToEntity(resourceDto);
        resource.setUser(user);

        Resource createdResource = resourceRepository.save(resource);
        return mapResourceToDto(createdResource);
    }

    @Override
    public List<ResourceDto> getAllResources() {
        List<Resource> resources = resourceRepository.findAll();
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException("No hay recursos disponibles");
        }
        return resources.stream().map(this::mapResourceToDto).toList();

    }

    @Override
    public String deleteResource(Integer resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        if (!isOwnerOrAdmin(resource, userEmail, authentication)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este recurso.");
        }
        resourceRepository.delete(resource);
        return "Recurso eliminado con éxito.";
    }

    @Override
    public ResourceDto updateResource(Integer resourceId, ResourceDto resourceDto) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if (!isOwnerOrAdmin(resource, userEmail, authentication)) {
            throw new AccessDeniedException("No tienes permiso para modificar este recurso");
        }
        resource.setTitle(resourceDto.getTitle());
        resource.setUrl(resourceDto.getUrl());
        resource.setDescription(resourceDto.getDescription());
        resource.setFile(resourceDto.getFile());

        Resource updatedResource = resourceRepository.save(resource);
        return mapResourceToDto(updatedResource);

    }

    @Override
    public ResourceDto getResourceById(Integer resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));
        return mapResourceToDto(resource);

    }

    private Resource mapResourceToEntity(ResourceDto resourceDto) {
        return modelMapper.map(resourceDto, Resource.class);
    }

    private ResourceDto mapResourceToDto(Resource resource) {
        ResourceDto resourceDto = modelMapper.map(resource, ResourceDto.class);
        resourceDto.setCreatorEmail(resource.getUser().getEmail());
        return resourceDto;
    }

    private boolean isOwnerOrAdmin(Resource resource, String userEmail, Authentication authentication) {
        boolean isOwner = resource.getUser().getEmail().equals(userEmail);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_FEMSENIORADMIN"));
        return isOwner || isAdmin;
    }
}
