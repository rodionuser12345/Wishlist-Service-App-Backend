package com.endava.wishlist.service;

import com.endava.wishlist.domain.dto.RegistrationRequest;
import com.endava.wishlist.domain.dto.WishRequest;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.UserRepository;
import com.endava.wishlist.repository.WishRepository;
import com.endava.wishlist.service.impl.UserServiceImpl;
import com.endava.wishlist.service.impl.WishServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class UnitTests {
    RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .fullName("Test Name")
            .email("test@email.com")
            .password("Password1")
            .build();

    User user = User.builder()
            .fullName("Test Name")
            .email("test@email.com")
            .password("Password1")
            .role("ROLE_USER")
            .build();

    User user2 = User.builder()
            .fullName("Test Name")
            .email("test@email.com")
            .password("Password1")
            .role("ROLE_USER")
            .build();

    Wish wish = Wish.builder()
            .title("validWish")
            .description("testing the edit wish")
            .build();
    @Mock
    UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    WishRepository wishRepository;

    @InjectMocks
    private WishServiceImpl wishService;

    @Test
    void testRegisterSuccessfullyPassedWhenEnterFilledRegistrationRequestWithNotUsedEmail() throws CustomUserServiceException {
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("Password1");
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        Assertions.assertEquals(userService.register(registrationRequest), user);
    }

    @Test
    void testRegisterFailedWhenEnterFilledRegistrationRequestWithEmailUsedBefore() {
        RegistrationRequest registrationRequest1 = RegistrationRequest.builder()
                .fullName("Test Name")
                .email("test1@email.com")
                .password("Password1")
                .build();

        when(passwordEncoder.encode(registrationRequest1.getPassword())).thenReturn("Password1");
        when(userRepository.findByEmail((registrationRequest1.getEmail()))).thenReturn(user);

        Assertions.assertThrows(CustomUserServiceException.class, () -> userService.register(registrationRequest1));
    }

    @Test
    void testGetAllSuccessWhenCallMethodReturnListOfUsers() {
        List<User> listOfUser = new ArrayList();
        listOfUser.add(user);
        listOfUser.add(user2);
        when(userRepository.findAll()).thenReturn(listOfUser);

        assertEquals(userService.getAll(), listOfUser);
    }

    @Test
    void testDeleteWhenGettingTheRightIdThenDeleteIt() {
        lenient().when(userRepository.findById((1L))).
                thenReturn(Optional.ofNullable(user));
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(1L, arg0);
            return null;
        }).when(userRepository).deleteById(any(Long.class));
        userRepository.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testFindByIdWhenExistsThenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(userService.findById(1L), user);
    }

    @Test
    void testFindByIdWhenDoesNotExistThenReturnNull() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertNull(userService.findById(2L));
    }

    @Test
    void testFindByEmailWhenExistsThenReturnUser() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(user);

        Assertions.assertEquals(userService.findByEmail("test@email.com"), user);
    }

    @Test
    void testFindByFullNameWhenExistsThenReturnUser() {
        when(userRepository.findByFullName("Test Name")).thenReturn(user);

        Assertions.assertEquals(userService.findByFullName("Test Name"), user);
    }

    @Test
    void testFindByEmailAndPasswordWhenEmailExistsAndPasswordIsCorrectThenReturnUser() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Assertions.assertEquals(userService.findByEmailAndPassword("test@email.com", "Password1"), user);
    }

    @Test
    void testFindByEmailAndPasswordWhenEmailExistsAndPasswordIsIncorrectThenReturnNull() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertNull(userService.findByEmailAndPassword("test@email.com", "Password1"));
    }

    @Test
    void testFindByEmailAndPasswordWhenEmailDoesNotExistThenReturnNull() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(null);

        assertNull(userService.findByEmailAndPassword("test@email.com", "Password1"));
    }

    @Test
    void testEditExistingItemWithValidRequest() throws CustomUserServiceException {
        when(wishRepository.findById(1L)).thenReturn(Optional.ofNullable(wish));
        WishRequest wish2 = WishRequest.builder()
                .title("some other title")
                .description(wish.getDescription())
                .build();
        Wish wishAfterEditing = Wish.builder()
                .id(wish.getId())
                .title(wish2.getTitle())
                .description(wish.getDescription())
                .image(wish.getImage())
                .build();

        assertEquals(wishService.editWish(1L, wish2), wishAfterEditing);
    }

    @Test
    void testEditingUnexistingItem() {
        when(wishRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        WishRequest wish2 = WishRequest.builder()
                .title("some other title")
                .description(wish.getDescription())
                .build();

        Assertions.assertThrows(CustomUserServiceException.class, () -> wishService.editWish(anyLong(), wish2));
    }

}