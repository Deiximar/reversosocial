package com.reversosocial.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.reversosocial.config.exception.CustomException;
import com.reversosocial.config.exception.EventFullException;
import com.reversosocial.config.exception.ResourceNotFoundException;
import com.reversosocial.config.exception.UsernameNotFoundException;
import com.reversosocial.models.dto.EventDto;
import com.reversosocial.models.entity.Event;
import com.reversosocial.models.entity.Sector;
import com.reversosocial.models.entity.User;
import com.reversosocial.repository.EventRepository;
import com.reversosocial.repository.SectorRepository;
import com.reversosocial.repository.UserRepository;
import com.reversosocial.service.EventService;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final SectorRepository sectorRepository;

  @Override
  public EventDto createEvent(EventDto eventDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean hasPermission = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("CREATE"));
    if (!hasPermission) {
      throw new AccessDeniedException("No tienes permiso para crear un evento.");
    }
    String userEmail = authentication.getName();
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
    Sector sector = sectorRepository.findBySector(eventDto.getSector())
        .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado"));

    Event event = mapEventToEntity(eventDto);
    event.setUser(user);
    event.setSector(sector);
    Event createdEvent = eventRepository.save(event);
    return mapEventToDto(createdEvent);
  }

  @Override
  public List<EventDto> getAllEvents() {
    List<Event> events = eventRepository.findAll();

    if (events.isEmpty()) {
      throw new ResourceNotFoundException("No hay eventos disponibles");
    }
    return events.stream().map(this::mapEventToDto).toList();
  }

  @Override
  public String deleteEvent(Integer eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = authentication.getName();

    if (!isOwnerOrAdmin(event, userEmail, authentication)) {
      throw new AccessDeniedException("No tienes permiso para eliminar este evento.");
    }
    eventRepository.delete(event);
    return "El evento ha sido eliminado exitosamente.";
  }

  @Override
  public EventDto updateEvent(Integer eventId, EventDto eventDto) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = authentication.getName();
    if (!isOwnerOrAdmin(event, userEmail, authentication)) {
      throw new AccessDeniedException("No tienes permiso para modificar este evento.");
    }
    Sector sector = sectorRepository.findBySector(eventDto.getSector())
        .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado."));
    event.setTitle(eventDto.getTitle());
    event.setDescription(eventDto.getDescription());
    event.setDate(eventDto.getDate());
    event.setTime(eventDto.getTime());
    event.setModality(eventDto.getModality());
    event.setLocation(eventDto.getLocation());
    event.setMaxParticipants(eventDto.getMaxParticipants());
    event.setSector(sector);
    Event updatedEvent = eventRepository.save(event);
    return mapEventToDto(updatedEvent);
  }

  @Override
  public EventDto getEventById(Integer eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado."));
    
    EventDto eventDto = mapEventToDto(event);
    
    // Obtener el usuario actual
    User currentUser = getCurrentUser();
    
    // Determinar si el usuario está suscrito al evento
    boolean isUserSubscribed = false;
    if (currentUser != null) {
      isUserSubscribed = event.getSubscriptors().stream()
      .anyMatch(user -> user.getId() == currentUser.getId());
    }
    
    // Establecer el campo isUserSubscribed en el EventDto
    eventDto.setUserSubscribed(isUserSubscribed);
    
    return eventDto;
}

  @Override
public String subscribeUserToEvent(Integer eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado."));

    User user = getCurrentUser(); 

    // Verifica si el evento ya está lleno.
    if (event.isEventFull()) {
        throw new EventFullException("El evento ya está lleno.");
    }

    // Verifica si el usuario ya está suscrito.
    if (event.getSubscriptors().contains(user)) {
        throw new CustomException("Ya estás suscrito a este evento.");
    }

    // Agrega el usuario a la lista de suscriptores y actualiza el estado de isFull.
    event.getSubscriptors().add(user);
    event.checkAndUpdateIsFull(); 

    eventRepository.save(event);
    return "¡Te has suscrito al evento " + event.getTitle() + " con éxito!";
}

  @Override
  public String unsubscribeUserToEvent(Integer eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado."));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = authentication.getName();
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

    if (event.getSubscriptors().contains(user)) {
      event.getSubscriptors().remove(user);
      event.checkAndUpdateIsFull();
      eventRepository.save(event);
    } else {
      throw new CustomException("No estás suscrito a este evento.");
    }
    return ("¡Te has desuscrito del evento " + event.getTitle() + " con exito!");
  }

  private Event mapEventToEntity(EventDto eventDto) {
    Event event = modelMapper.map(eventDto, Event.class);
    return event;
  }

  private EventDto mapEventToDto(Event event) {
    EventDto eventDto = modelMapper.map(event, EventDto.class);
    eventDto.setCreatorEmail(event.getUser().getEmail());
    eventDto.setEventFull(event.isEventFull()); // Asegura que este método sea reconocido y se utilice correctamente
    return eventDto;
}
  private boolean isOwnerOrAdmin(Event event, String userEmail, Authentication authentication) {
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_FEMSENIORADMIN"));
    return event.getUser().getEmail().equals(userEmail) || isAdmin;
  }

  @Override
    public List<EventDto> searchEventsByTitle(String title) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCase(title);
        return events.stream()
                     .map(this::mapEventToDto)  
                     .collect(Collectors.toList()); 
    }
  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    String userEmail = authentication.getName();
    return userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
}
  }
