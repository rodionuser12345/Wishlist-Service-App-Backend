package com.endava.wishlist.service;

import com.endava.wishlist.domain.dto.EventRequest;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.EventRepository;
import com.endava.wishlist.service.impl.EventServiceImpl;
import com.endava.wishlist.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventUnitTests {

    @Mock
    private EventRepository repository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    Authentication auth;

    @InjectMocks
    private EventServiceImpl eventService;

    User user = User.builder()
            .fullName("Test Name")
            .email("test@email.com")
            .password("Password1")
            .role("ROLE_USER")
            .build();

    EventRequest eventRequest = EventRequest.builder()
            .type("DR")
            .privacy("public")
            .date(LocalDate.now())
            .description("desc")
            .title("title")
            .build();

    Event event = Event.builder()
            .user(user)
            .title(eventRequest.getTitle())
            .type(eventRequest.getType())
            .description(eventRequest.getDescription())
            .date(eventRequest.getDate())
            .privacy(eventRequest.getPrivacy())
            .build();

    @Test
    void testAddWishListSuccessfullyWhenUserExists() throws CustomUserServiceException {

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(repository.save(event)).thenReturn(event);

        assertEquals(eventService.saveEvent(eventRequest, user.getEmail()), event);
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testAddWishListUnSuccessfullyWhenUserDoesNotExist() {

        when(userService.findByEmail(anyString())).thenReturn(null);

        assertThrows(CustomUserServiceException.class, () -> eventService.saveEvent(eventRequest, anyString()));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testAddWishListUnSuccessfullyWhenTitleIsEmpty() {

        EventRequest eventInvalidRequest = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .build();

        Event invalidEvent = Event.builder()
                .user(user)
                .title(eventInvalidRequest.getTitle())
                .type(eventInvalidRequest.getType())
                .description(eventInvalidRequest.getDescription())
                .date(eventInvalidRequest.getDate())
                .privacy(eventInvalidRequest.getPrivacy())
                .build();

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(repository.save(invalidEvent)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.saveEvent(eventInvalidRequest, user.getEmail()));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testAddWishListUnSuccessfullyWhenTypeIsEmpty() {

        EventRequest eventInvalidRequest = EventRequest.builder()
                .title("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .build();

        Event invalidEvent = Event.builder()
                .user(user)
                .title(eventInvalidRequest.getTitle())
                .type(eventInvalidRequest.getType())
                .description(eventInvalidRequest.getDescription())
                .date(eventInvalidRequest.getDate())
                .privacy(eventInvalidRequest.getPrivacy())
                .build();

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(repository.save(invalidEvent)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.saveEvent(eventInvalidRequest, user.getEmail()));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testAddWishListUnSuccessfullyWhenDescriptionIsEmpty() {

        EventRequest eventInvalidRequest = EventRequest.builder()
                .title("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .build();

        Event invalidEvent = Event.builder()
                .user(user)
                .title(eventInvalidRequest.getTitle())
                .type(eventInvalidRequest.getType())
                .description(eventInvalidRequest.getDescription())
                .date(eventInvalidRequest.getDate())
                .privacy(eventInvalidRequest.getPrivacy())
                .build();

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(repository.save(invalidEvent)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.saveEvent(eventInvalidRequest, user.getEmail()));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testGetWishListByIdSuccessfully() throws CustomUserServiceException {

        when(repository.findById(eq(0L))).thenReturn(Optional.ofNullable(event));

        assertEquals(event, eventService.findById(eq(0L)));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testGetWishListByIdUnSuccessfully() {

        when(repository.findById(eq(0L))).thenReturn(Optional.empty());

        assertThrows(CustomUserServiceException.class, () -> eventService.findById(eq(0L)));
        verifyNoMoreInteractions(repository, userService);
    }


    @Test
    void testGetAllWishListsSuccessfully() {
        List<Event> eventList = new ArrayList<>();

        Event eventSecond = Event.builder()
                .user(user)
                .title(eventRequest.getTitle())
                .type(eventRequest.getType())
                .description(eventRequest.getDescription())
                .date(eventRequest.getDate())
                .privacy(eventRequest.getPrivacy())
                .build();

        Event eventThird = Event.builder()
                .user(user)
                .title(eventRequest.getTitle())
                .type(eventRequest.getType())
                .description(eventRequest.getDescription())
                .date(eventRequest.getDate())
                .privacy(eventRequest.getPrivacy())
                .build();

        eventList.add(event);
        eventList.add(eventSecond);
        eventList.add(eventThird);

        when(auth.getCredentials()).thenReturn("test@email.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        SecurityContextHolder.getContext().getAuthentication().getCredentials();
        when(repository.findAllByEmail(anyString())).thenReturn(eventList);

        assertEquals(eventList, eventService.getAll());
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testEditWishListSuccessfully() throws CustomUserServiceException {

        Event newEvent = Event.builder()
                .user(user)
                .title("new-title")
                .type("new-type")
                .description("new-descr")
                .date(eventRequest.getDate())
                .privacy(eventRequest.getPrivacy())
                .build();

        when(repository.findById(eq(0L))).thenReturn(Optional.ofNullable(event));
        when(repository.save(event)).thenReturn(newEvent);

        assertEquals(newEvent, eventService.changeById(0L, eventRequest));
        verifyNoMoreInteractions(repository, userService);
    }


    @Test
    void testEditWishListUnSuccessfullyNoWishListWithSpecifiedID() {

        when(repository.findById(eq(0L))).thenReturn(Optional.empty());

        assertThrows(CustomUserServiceException.class, () -> eventService.changeById(eq(0L), eventRequest));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testEditWishListUnSuccessfullyTitleIsEmpty() {

        EventRequest eventInvalidRequest = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .build();

        Event invalidEvent = Event.builder()
                .user(user)
                .title(eventInvalidRequest.getTitle())
                .type(eventInvalidRequest.getType())
                .description(eventInvalidRequest.getDescription())
                .date(eventInvalidRequest.getDate())
                .privacy(eventInvalidRequest.getPrivacy())
                .build();

        when(repository.findById(eq(0L))).thenReturn(Optional.ofNullable(event));
        when(repository.save(invalidEvent)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.changeById(eq(0L), eventInvalidRequest));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testEditWishListUnSuccessfullyTypeIsEmpty() {

        EventRequest eventInvalidRequest = EventRequest.builder()
                .title("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .build();

        Event invalidEvent = Event.builder()
                .user(user)
                .title(eventInvalidRequest.getTitle())
                .type(eventInvalidRequest.getType())
                .description(eventInvalidRequest.getDescription())
                .date(eventInvalidRequest.getDate())
                .privacy(eventInvalidRequest.getPrivacy())
                .build();

        when(repository.findById(eq(0L))).thenReturn(Optional.ofNullable(event));
        when(repository.save(invalidEvent)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> eventService.changeById(eq(0L), eventInvalidRequest));
        verifyNoMoreInteractions(repository, userService);
    }

    @Test
    void testDeleteByIdWishListSuccessfully() throws CustomUserServiceException {

        when(repository.findById(0L)).thenReturn(Optional.ofNullable(event));
        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(0L, arg0);
            return null;
        }).when(repository).deleteById(0L);

        eventService.deleteById(0L);

        verify(repository, times(1)).deleteById(0L);
        verifyNoMoreInteractions(repository, userService);

    }

    @Test
    void testDeleteByIdWishListUnSuccessfullyNoWishListWithSuchId() {

        when(repository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(CustomUserServiceException.class, () -> eventService.deleteById(0L));

        verifyNoMoreInteractions(repository, userService);

    }
}
