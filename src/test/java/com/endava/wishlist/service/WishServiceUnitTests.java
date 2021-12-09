package com.endava.wishlist.service;

import com.endava.wishlist.domain.dto.WishRequest;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.WishRepository;
import com.endava.wishlist.service.impl.WishServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishServiceUnitTests {

    User user = User.builder()
            .fullName("Test Name")
            .email("test@email.com")
            .password("Password1")
            .role("ROLE_USER")
            .build();

    Event event = Event.builder()
            .user(user)
            .id(1L)
            .title("evemt1")
            .type("type1")
            .description("description1")
            .date(LocalDate.now())
            .privacy("public")
            .build();

    Wish wish1 = Wish.builder()
            .title("iphone")
            .description("description1")
            .image("image1.jpg")
            .event(event)
            .build();

    Wish wish2 = Wish.builder()
            .description("description2")
            .image("image2.jpg")
            .event(event)
            .build();

    WishRequest wishRequest1 = WishRequest.builder()
            .title("iphone")
            .description("description1")
            .image("image1.jpg")
            .build();

    WishRequest wishRequest2 = WishRequest.builder()
            .description("description2")
            .image("image2.jpg")
            .build();


    @Mock
    WishRepository wishRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private WishServiceImpl wishService;


    @Test
    void testAddWishSuccessfullyWhenEventExists() throws CustomUserServiceException {
        when(eventService.findById(event.getId())).thenReturn(event);
        when(wishRepository.save(wish1)).thenReturn(wish1);

        assertEquals(wishService.saveWish(wishRequest1, event.getId()), wish1);
    }

    @Test
    void testAddWishFailWhenEventDoesNotExists() throws CustomUserServiceException {
        when(eventService.findById(anyLong())).thenReturn(null);

        Exception actualException = assertThrows(CustomUserServiceException.class, () -> wishService.saveWish(wishRequest1, anyLong()));

        assertEquals("Event not Found", actualException.getMessage());
    }

    @Test
    void testAddWishFailWhenTitleIsEmpty() throws CustomUserServiceException {
        when(eventService.findById(event.getId())).thenReturn(event);
        when(wishRepository.save(wish2)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> wishService.saveWish(wishRequest2, event.getId()));
    }

    @Test
    void testDeleteByIdWishSuccessfully() throws CustomUserServiceException {

        when(wishRepository.findById(0L)).thenReturn(Optional.ofNullable(wish1));
        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(0L, arg0);
            return null;
        }).when(wishRepository).deleteById(0L);

        wishService.deleteWish(0L);

        verify(wishRepository, times(1)).deleteById(0L);
    }

    @Test
    void testDeleteByIdWishFailOnNoWishWithSuchId() {

        when(wishRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(CustomUserServiceException.class, () -> wishService.deleteWish(0L));

    }
}
