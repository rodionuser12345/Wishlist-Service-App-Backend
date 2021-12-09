package com.endava.wishlist.service;


import com.endava.wishlist.domain.dto.EventRequest;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.exception.CustomUserServiceException;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Event saveEvent(EventRequest eventRequest, String email) throws CustomUserServiceException;

    void deleteById(Long id) throws CustomUserServiceException;

    Optional<Event> findByTitle(String title);
    Event findById(Long id) throws CustomUserServiceException;

    List<Event> getAll();

    Event changeById(Long id, EventRequest request) throws CustomUserServiceException;
}