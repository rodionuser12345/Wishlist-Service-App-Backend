package com.endava.wishlist.web.controller;

import com.endava.wishlist.Application;
import com.endava.wishlist.domain.dto.AuthRequest;
import com.endava.wishlist.domain.dto.EventRequest;
import com.endava.wishlist.domain.dto.RegistrationRequest;
import com.endava.wishlist.domain.dto.WishRequest;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.service.UserService;
import com.endava.wishlist.service.WishService;
import com.endava.wishlist.service.impl.EventServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IntegrationTests extends AbstractTransactionalJUnit4SpringContextTests {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private WishService wishService;

    @Autowired
    IntegrationTests(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private User createValidRegistrationRequest(String email) throws CustomUserServiceException {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("Created User")
                .email(email)
                .password("abcdE12")
                .build();
        return userService.register(request);
    }
    @Test
    @SneakyThrows
    void registerWithValidUser() {
        User registrationValidUser = new User();
        registrationValidUser.setFullName("Elizaa Bacaaa");
        registrationValidUser.setEmail("elizafg65hfh.baca@gmail.com");
        registrationValidUser.setPassword("passworhd");
        registrationValidUser.setRole("ROLE_USER");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationValidUser)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(userService.findByEmail(registrationValidUser.getEmail()).getEmail(), registrationValidUser.getEmail());
    }

    @Test
    @SneakyThrows
    void registerWithInvalidUsernoFullName() {
        User userInvalidNoFullName = new User();
        userInvalidNoFullName.setEmail("hg98jd@gmail.com");
        userInvalidNoFullName.setPassword("76465476");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userInvalidNoFullName)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertNull(userService.findByEmail(userInvalidNoFullName.getEmail()));
    }

    @Test
    @SneakyThrows
    void registerWithInvalidUserNotUniqueEmail() {
        createValidRegistrationRequest("valid.email@endava.com");

        RegistrationRequest reqistrationRequest = RegistrationRequest.builder()
                .fullName("Valid FullName")
                .email("valid.email@endava.com")
                .password("password")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqistrationRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertNull(userService.findByFullName(reqistrationRequest.getFullName()));
    }

    @Test
    @SneakyThrows
    void registerWithInvalidUserNoFullName() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .email("validEmail@endava.com")
                .password("password")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertNull(userService.findByFullName(registrationRequest.getFullName()));
    }

    @Test
    @SneakyThrows
    void loginWithValidRequest() {
        createValidRegistrationRequest("testLogin@gmail.com");

        AuthRequest authRequest = AuthRequest.builder()
                .email("testLogin@gmail.com")
                .password("abcdE12")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void loginWithInvalidRequest() {
        AuthRequest authRequest = AuthRequest.builder()
                .email("fghfghfh@mail.com")
                .password("457689")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void loginWithInvalidEmailRequestShouldThrowCorrespondedException() {
        AuthRequest authRequest = AuthRequest.builder()
                .email("fghfghfhhhh")
                .password("457689")
                .build();
        
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'field':'email', 'message':'must be a well-formed email address'}]"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void registerWithNoEmailAndNoPasswordRequestShouldThrowCorrespondedException() {
        AuthRequest authRequest = AuthRequest.builder()
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'field':'email', 'message':'must not be empty'}, {'field':'password', 'message':'must not be empty'}]"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void editExistingItemWithValidContent() {
        RegistrationRequest reqistrationRequest = RegistrationRequest.builder()
                .fullName("Valid FullName")
                .email("valid.email@endava.com")
                .password("password")
                .build();
        userService.register(reqistrationRequest);

        AuthRequest authRequest = AuthRequest.builder()
                .email("valid.email@endava.com")
                .password(reqistrationRequest.getPassword())
                .build();
        userService.login(authRequest);

        EventRequest eventRequest = EventRequest.builder()
                .title("the testing title")
                .type("testing type")
                .description("testing description")
                .privacy("PUBLIC")
                .build();
        eventService.saveEvent(eventRequest, "valid.email@endava.com");

        Event eventFromDB = eventService.findByTitle(eventRequest.getTitle()).get();

        WishRequest wishrequest = WishRequest.builder()
                .title("validWish")
                .description("testing the edit wish")
                .build();
        WishRequest wishRequestToEdit = WishRequest.builder()
                .title("another title")
                .build();
        wishService.saveWish(wishrequest, eventFromDB.getId());

        Wish wishFromDb = wishService.findByTitle(wishrequest.getTitle()).get();

        mockMvc.perform(MockMvcRequestBuilders.put("/wishes/" + wishFromDb.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequestToEdit)))
                .andExpect(status().isOk());

        assertEquals(wishService.findByTitle(wishRequestToEdit.getTitle()).get().getTitle(), wishRequestToEdit.getTitle());
    }

    @Test
    @SneakyThrows
    public void editUnexistingItemThrowException() {
        RegistrationRequest reqistrationRequest = RegistrationRequest.builder()
                .fullName("Valid FullName")
                .email("valid.email@endava.com")
                .password("password")
                .build();
        userService.register(reqistrationRequest);

        AuthRequest authRequest = AuthRequest.builder()
                .email("valid.email@endava.com")
                .password(reqistrationRequest.getPassword())
                .build();
        userService.login(authRequest);

        WishRequest wishRequestToEdit = WishRequest.builder()
                .title("another title")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/wishes/" + anyLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequestToEdit)))
                .andExpect(status().is4xxClientError());
        assertThrows(CustomUserServiceException.class, () -> wishService.editWish(anyLong(), wishRequestToEdit));
    }

    @Test
    @SneakyThrows
    public void editExistingItemWithInvalidContent() {
        RegistrationRequest reqistrationRequest = RegistrationRequest.builder()
                .fullName("Valid FullName")
                .email("valid.email@endava.com")
                .password("password")
                .build();
        userService.register(reqistrationRequest);

        AuthRequest authRequest = AuthRequest.builder()
                .email("valid.email@endava.com")
                .password(reqistrationRequest.getPassword())
                .build();
        userService.login(authRequest);

        EventRequest eventRequest = EventRequest.builder()
                .title("the testing title")
                .type("testing type")
                .description("testing description")
                .privacy("PUBLIC")
                .build();
        eventService.saveEvent(eventRequest, "valid.email@endava.com");

        Event eventFromDB = eventService.findByTitle(eventRequest.getTitle()).get();

        WishRequest wishrequest = WishRequest.builder()
                .title("validWish")
                .description("testing the edit wish")
                .build();
        WishRequest wishRequestToEdit = WishRequest.builder()
                .build();
        wishService.saveWish(wishrequest, eventFromDB.getId());

        Wish wishFromDb = wishService.findByTitle(wishrequest.getTitle()).get();

        mockMvc.perform(MockMvcRequestBuilders.put("/wishes/" + wishFromDb.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequestToEdit)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'field':'title', 'message':'Title is empty!'}]"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void addValidWishList() {
        createValidRegistrationRequest("intTestOne@gmail.com");

        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wishlists/intTestOne@gmail.com/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Save"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void addInvalidWishList() {
        createValidRegistrationRequest("intTestTwo@gmail.com");

        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wishlists/intTestTwo@gmail.com/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'field':'title','message':'Title is empty!'}]"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteWishListSuccessfully() {
        User user = createValidRegistrationRequest("intTestThree@gmail.com");

        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        eventService.saveEvent(request, user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.delete("/wishlists/" + eventService.findByTitle(request.getTitle()).get().getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @SneakyThrows
    void deleteWishListUnSuccessfullyNoSuchWishlist() {
        createValidRegistrationRequest("intTestFour@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.delete("/wishlists/delete/12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWishListByIdSuccessfully() {
        User user = createValidRegistrationRequest("intTestFive@gmail.com");

        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        eventService.saveEvent(request, user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get("/wishlists/wishlist/" + eventService.findByTitle(request.getTitle()).get().getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWishListByIdUnSuccessfully() {
        User user = createValidRegistrationRequest("intTestSix@gmail.com");

        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        eventService.saveEvent(request, user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get("/wishlists/wishlist/6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getAllWishListsSuccessfully() {

        List<EventRequest> requests = new ArrayList<>();


        User user = createValidRegistrationRequest("intTestSeven@gmail.com");

        EventRequest requestFirst = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        eventService.saveEvent(requestFirst, user.getEmail());

        EventRequest requestSecond = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        eventService.saveEvent(requestSecond, user.getEmail());

        requests.add(requestFirst);
        requests.add(requestSecond);

        mockMvc.perform(MockMvcRequestBuilders.get("/wishlists")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbnRUZXN0U2V2ZW5AZ21haWwuY29tIiwiZXhwIjoxNjIzMzU4ODAwLCJmdWxsTmFtZSI6IkNyZWF0ZWQgVXNlciJ9.wEnyod7HFBeyZn5-YUnL8-ZoITsyCly8Zk_cgnQxkHZSBakRQl29d55a-MsdLlywbXKBRh4_k70qVtBAks6JYg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requests)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWishListByIdWithPrivacyPrivateBadRequest() {
        User user = createValidRegistrationRequest("intTest@gmail.com");
        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("PRIVATE")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();
        eventService.saveEvent(request, user.getEmail());

        RegistrationRequest otherUser = RegistrationRequest.builder()
                .fullName("The FullName")
                .email("valid.email@gmail.com")
                .password("abcdE12")
                .build();
        userService.register(otherUser);

        final ResultActions authorization = mockMvc.perform(MockMvcRequestBuilders.get("/wishlists/wishlist/" + eventService.findByTitle(request.getTitle()).get().getId())
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YWxpZC5lbWFpbEBnbWFpbC5jb20iLCJleHAiOjE2MjM3OTA4MDAsImZ1bGxOYW1lIjoiVGhlIEZ1bGxOYW1lIn0.fBKkAX8fPJGnYwLXyzqLm64wFyD_qnqOx-wr5tJdsv9GCvihNE5h2bVYvZlXkM_jRVb3s-jcA_d0crLPIbNX9g"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWishListByIdWithPrivacyPublic() {
        User user = createValidRegistrationRequest("intTest@gmail.com");
        EventRequest request = EventRequest.builder()
                .type("DR")
                .privacy("PUBLIC")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();
        eventService.saveEvent(request, user.getEmail());

        RegistrationRequest otherUser = RegistrationRequest.builder()
                .fullName("The FullName")
                .email("valid.email@gmail.com")
                .password("abcdE12")
                .build();
        userService.register(otherUser);

        final ResultActions authorization = mockMvc.perform(MockMvcRequestBuilders.get("/wishlists/wishlist/" + eventService.findByTitle(request.getTitle()).get().getId())
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YWxpZC5lbWFpbEBnbWFpbC5jb20iLCJleHAiOjE2MjM3OTA4MDAsImZ1bGxOYW1lIjoiVGhlIEZ1bGxOYW1lIn0.fBKkAX8fPJGnYwLXyzqLm64wFyD_qnqOx-wr5tJdsv9GCvihNE5h2bVYvZlXkM_jRVb3s-jcA_d0crLPIbNX9g"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @SneakyThrows
    void editWishListsByIdSuccessfully() {
        User user = createValidRegistrationRequest("intTestEight@gmail.com");

        EventRequest oldEvent = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        EventRequest changedEvent = EventRequest.builder()
                .type("test-type")
                .privacy("public")
                .date(LocalDate.now())
                .description("test-desc")
                .title("test-title")
                .build();

        eventService.saveEvent(oldEvent, user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.put("/wishlists/" + eventService.findByTitle(oldEvent.getTitle()).get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changedEvent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changedEvent)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void editWishListsByIdUnSuccessfully() {
        User user = createValidRegistrationRequest("intTestNine@gmail.com");

        EventRequest oldEvent = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();

        EventRequest changedEvent = EventRequest.builder()
                .type("test-type")
                .privacy("public")
                .date(LocalDate.now())
                .description("test-desc")
                .build();

        eventService.saveEvent(oldEvent, user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.put("/wishlists/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changedEvent)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'field':'title','message':'Title is empty!'}]"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void addValidWish() {
        createValidRegistrationRequest("exampleofMail@gmail.com");

        EventRequest eventRequest = EventRequest.builder()
                .type("DR")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc")
                .title("title")
                .build();
        eventService.saveEvent(eventRequest, "exampleofMail@gmail.com");

        WishRequest wishRequest = WishRequest.builder()
                .title("title1")
                .description("description1")
                .image("image1.jpg")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wishes/" + eventService.findByTitle(eventRequest.getTitle()).get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequest)))
                .andExpect(status().isAccepted())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void addInvalidWish() {
        createValidRegistrationRequest("exampleofMail2@gmail.com");
        WishRequest wishRequest2 = WishRequest.builder()
                .description("description1")
                .image("image1.jpg")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wishes/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequest2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'field':'title','message':'Title is empty!'}]"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void addWishToUnexistingEvent() {
        createValidRegistrationRequest("exampleofMail33@gmail.com");
        WishRequest wishRequest = WishRequest.builder()
                .title("title1")
                .description("description1")
                .image("image1.jpg")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wishes/122")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'message':'No such wishlist has found'}"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteWishSuccessfully() {
        User user = createValidRegistrationRequest("exampleofmail9@gmail.com");

        EventRequest eventRequest = EventRequest.builder()
                .type("type1")
                .privacy("public")
                .date(LocalDate.now())
                .description("desc1")
                .title("title1")
                .build();

        Event event = eventService.saveEvent(eventRequest, user.getEmail());

        WishRequest wishRequest = WishRequest.builder()
                .title("title1")
                .description("description1")
                .image("image1.jpg")
                .build();

        wishService.saveWish(wishRequest, event.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/wishes/" + wishService.findByTitle(wishRequest.getTitle()).get().getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @SneakyThrows
    void deleteWishFailOnNoSuchWishlist() {
        createValidRegistrationRequest("exampleofmail8@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.delete("/wishes/9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWisByIdSuccessfully() {
        User user = createValidRegistrationRequest("emailAlexei1@gmail.com");

        WishRequest wishRequest = WishRequest.builder()
                .title("title11")
                .description("description 11")
                .image("www.image1.com")
                .link(null)
                .price(BigDecimal.ZERO)
                .currency("MDL")
                .priority("Want to have")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/wishes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(wishRequest)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWisByIdFailIfIdNotExist() {
        User user = createValidRegistrationRequest("emailAlexei1@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.get("/wishes/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


}