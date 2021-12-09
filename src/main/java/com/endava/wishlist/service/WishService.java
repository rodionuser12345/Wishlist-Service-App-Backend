package com.endava.wishlist.service;

import com.endava.wishlist.domain.dto.WishRequest;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;

import java.util.List;
import java.util.Optional;

public interface WishService {
    Wish saveWish(WishRequest wishRequest, Long id) throws CustomUserServiceException;

    void deleteWish(Long id) throws CustomUserServiceException;

    Wish editWish(Long id, WishRequest wishRequest) throws CustomUserServiceException;

    Optional<Wish> findByTitle(String title);

    List<Wish> getAllWishes() throws CustomUserServiceException;

    List<Wish> getWishesByEventId(Long id) throws CustomUserServiceException;

    Wish getWishById(Long id) throws CustomUserServiceException;
}
