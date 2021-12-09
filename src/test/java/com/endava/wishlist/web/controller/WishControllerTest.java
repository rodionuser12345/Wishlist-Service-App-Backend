package com.endava.wishlist.web.controller;

import com.endava.wishlist.domain.dto.WishResponse;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.service.WishService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class WishControllerTest {
    @InjectMocks
    private WishController wishControllerTest;

    @Mock
    private WishService wishService;

    private MockMvc mockMvc;
    private Event event1;
    private User user1;
    private Wish wish1;
    private Wish wish2;
    private WishResponse wishResponse1;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wishControllerTest).build();

        user1 = User.builder()
                .fullName("User Name1")
                .email("user1@gmail.com")
                .password("Password1")
                .role("ROLE_USER")
                .build();

        event1 = Event.builder()
                .user(user1)
                .id(1L)
                .title("event1")
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

        wishResponse1 = WishResponse.builder()
                        .title("response1")
                        .description("response1")
                        .image("response1")
                        .build();
    }

    @Test
    public void testGetWishByIdReturnOkIfPresent() throws Exception {
        when(wishService.getWishById(1L)).thenReturn(wish1);
        mockMvc.perform(MockMvcRequestBuilders.get("/wishes/1")).andExpect(status().isOk());
    }

    @Test
    public void getWishesByWishlistId() throws Exception {
        List<Wish> wishList = new ArrayList<>();
        wishList.add(wish1);
        wishList.add(wish2);

        when(wishService.getWishesByEventId(1L)).thenReturn(wishList);
        mockMvc.perform(MockMvcRequestBuilders.get("/wishes/wishlist/1")).andExpect(status().isOk());
    }
}