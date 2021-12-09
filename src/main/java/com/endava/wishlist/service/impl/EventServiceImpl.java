package com.endava.wishlist.service.impl;

import com.endava.wishlist.domain.dto.EventRequest;
import com.endava.wishlist.domain.dto.WishRequest;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.EventRepository;
import com.endava.wishlist.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public Event saveEvent(EventRequest eventRequest, String email) throws CustomUserServiceException {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new CustomUserServiceException("User not found", HttpStatus.BAD_REQUEST);
        }
        Event event = Event.builder()
                .user(user)
                .title(eventRequest.getTitle())
                .type(eventRequest.getType())
                .description(eventRequest.getDescription())
                .date(eventRequest.getDate())
                .privacy(eventRequest.getPrivacy())
                .build();

        eventRepository.save(event);
        log.info("IN saveEvent: successfully saved event - {}", event.getDescription());

        return event;
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws CustomUserServiceException {
        if (!eventRepository.findById(id).isPresent()) {
            log.warn("IN deleteById: no event found with id: {}", id);
            throw new CustomUserServiceException("No such wishlist", HttpStatus.BAD_REQUEST);
        }

        if (!SecurityContextHolder.getContext().getAuthentication().getCredentials().toString().equals(eventRepository.findOwner(id))){
            throw new CustomUserServiceException("You have no permission", HttpStatus.BAD_REQUEST);
        }

        eventRepository.deleteById(id);
        log.info("IN deleteById: successfully deleted event with id - {}", id);
    }

    @Override
    public Event findById(Long id) throws CustomUserServiceException {
        if (!eventRepository.findById(id).isPresent()) {
            log.warn("IN findById: no event has found with id: {}", id);
            throw new CustomUserServiceException("No such wishlist has found", HttpStatus.BAD_REQUEST);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getCredentials().toString().equals(eventRepository.findOwner(id))
                && eventRepository.checkPrivacy(id).equals("PRIVATE")){
            throw new CustomUserServiceException("You have no permission", HttpStatus.BAD_REQUEST);
        }
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public List<Event> getAll() {
        String email = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        log.info("IN getAll");
        return eventRepository.findAllByEmail(email);
    }

    @Override
    @Transactional
    public Event changeById(Long id, EventRequest eventRequest) throws CustomUserServiceException {
        if (!eventRepository.findById(id).isPresent()) {
            log.warn("IN changeById: no Event found with id: {}", id);
            throw new CustomUserServiceException("No such Event", HttpStatus.BAD_REQUEST);
        }

        if (!SecurityContextHolder.getContext().getAuthentication().getCredentials().toString().equals(eventRepository.findOwner(id))){
            throw new CustomUserServiceException("You have no permission", HttpStatus.BAD_REQUEST);
        }

        Event event = eventRepository.findById(id).get();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setDate(eventRequest.getDate());
        event.setPrivacy(eventRequest.getPrivacy());
        event.setType(eventRequest.getType());

        eventRepository.save(event);
        log.info("IN changeById: successfully edited event - {}", event.getTitle());

        return event;
    }

    @Override
    public Optional<Event> findByTitle(String title) {
        return eventRepository.findByTitle(title);
    }
}
