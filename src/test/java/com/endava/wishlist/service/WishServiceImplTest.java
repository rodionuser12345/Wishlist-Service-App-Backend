package com.endava.wishlist.service;

import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.WishRepository;
import com.endava.wishlist.service.impl.WishServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishServiceImplTest {

    @Mock
    private WishRepository wishRepository;

    @InjectMocks
    private WishServiceImpl wishService;

    private User user1;
    private Event event1;
    private Wish wish1;
    private Wish wish2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .fullName("User Name1")
                .email("user1@gmail.com")
                .password("Password1")
                .role("ROLE_USER")
                .build();

        event1 = Event.builder()
                .user(user1)
                .id(1L)
                .title("evemt1")
                .type("type1")
                .description("description1")
                .date(LocalDate.now())
                .privacy("public")
                .build();

        wish1 = Wish.builder()
                .title("title1")
                .description("description1")
                .image("image1")
                .event(event1)
                .build();

        wish2 = Wish.builder()
                .title("title2")
                .description("description2")
                .image("image2")
                .event(event1)
                .build();
    }

    @Test
    void testGetWishesByEventIdReturnListIfEventExist() {
        List<Wish> wishList = new ArrayList<>();
        wishList.add(wish1);
        wishList.add(wish2);

        when(wishRepository.findWishesByEventId(1L)).thenReturn(wishList);
        List<Wish> resultList = wishRepository.findWishesByEventId(1L);

        assertThat(resultList).containsAnyElementsOf(wishList);
    }
    @Test
    void testGetWishesByEventIdReturnExceptionIfWishListIsEmpty() {
        when(wishRepository.findWishesByEventId(1L)).thenReturn(new ArrayList<>());
        Exception actualException = assertThrows(CustomUserServiceException.class, () -> wishService.getWishesByEventId(1L));

        assertEquals("No items for such wishlist", actualException.getMessage());
    }
    @Test
    void testGetWishByIdReturnWishIfIdExist() throws CustomUserServiceException {
        when(wishRepository.findWishById(1L)).thenReturn(wish1);
        when(wishService.getWishById(1L)).thenReturn(wish1);

        assertEquals(wishService.getWishById(1L),wish1);
    }
    @Test
    void testGetWishByIdReturnExceptionIfIdNotExist() throws CustomUserServiceException {
        when(wishRepository.findWishById(2L)).thenReturn(null);

        Exception actualException = assertThrows(CustomUserServiceException.class, () -> wishService.getWishById(2L));

        assertEquals("No item for such id", actualException.getMessage());
    }
}