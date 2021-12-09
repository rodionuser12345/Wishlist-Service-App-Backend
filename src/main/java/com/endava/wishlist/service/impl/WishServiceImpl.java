package com.endava.wishlist.service.impl;

import com.endava.wishlist.domain.dto.WishRequest;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.domain.model.Wish;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.WishRepository;
import com.endava.wishlist.service.EventService;
import com.endava.wishlist.service.WishService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class WishServiceImpl implements WishService {

    private final EventService eventService;

    private final WishRepository wishRepository;

    @Override
    @Transactional
    public Wish saveWish(WishRequest wishRequest, Long id) throws CustomUserServiceException {
        if (eventService.findById(id) == null) {
            throw new CustomUserServiceException("Event not Found", HttpStatus.BAD_REQUEST);
        }

        Event event = eventService.findById(id);
        Wish wish = Wish.builder()
                .event(event)
                .description(wishRequest.getDescription())
                .title(wishRequest.getTitle())
                .image(wishRequest.getImage())
                .price(wishRequest.getPrice())
                .link(wishRequest.getLink())
                .currency(wishRequest.getCurrency())
                .priority(wishRequest.getPriority())
                .build();

        wishRepository.save(wish);
        log.info("IN saveEvent: successfully saved wish - {}", wish.getDescription());

        return wish;
    }

    @Override
    @Transactional
    public void deleteWish(Long id) throws CustomUserServiceException {
        if (!wishRepository.findById(id).isPresent()) {
            log.warn("IN deleteWish: no wish found with id: {}", id);
            throw new CustomUserServiceException("No such wish", HttpStatus.BAD_REQUEST);
        }
        wishRepository.deleteById(id);
            log.info("IN deleteWish: successfully deleted wish with id - {}", id);
    }

    @Override
    @Transactional
    public Wish editWish(Long wishId, WishRequest wishRequest) throws CustomUserServiceException {
        if (!wishRepository.findById(wishId).isPresent()) {
            log.warn("IN editWish: no wish found with id: {}", wishId);
            throw new CustomUserServiceException("No such wish", HttpStatus.BAD_REQUEST);
        }
        Wish wish = wishRepository.findById(wishId).get();
        wish.setTitle(wishRequest.getTitle());
        wish.setDescription(wishRequest.getDescription());
        wish.setImage(wishRequest.getImage());
        wish.setCurrency(wishRequest.getCurrency());
        wish.setLink(wishRequest.getLink());
        wish.setPrice(wishRequest.getPrice());
        wish.setPriority(wishRequest.getPriority());

        wishRepository.save(wish);
        log.info("IN editWish: successfully edited wish - {}", wish.getTitle());

        return wish;
    }

    @Override
    public Optional<Wish> findByTitle(String title) {
        return wishRepository.findByTitle(title);
    }

    @Override
    public List<Wish> getAllWishes() throws CustomUserServiceException {
        List<Wish> result = wishRepository.findAll();
        if(result.isEmpty()){
            log.warn("IN getAllWishes - no items found");
            throw new CustomUserServiceException("No items found", HttpStatus.BAD_REQUEST);
        }

        log.info("IN getAllWishes - get all items");
        return result;
    }

    @Override
    public List<Wish> getWishesByEventId(Long eventId) throws CustomUserServiceException {
        List<Wish> result = wishRepository.findWishesByEventId(eventId);
        if(result.isEmpty()){
            log.warn("IN getWishesByEventId - no items found by id: {}", eventId);
            throw new CustomUserServiceException("No items for such wishlist", HttpStatus.BAD_REQUEST);
        }

        log.info("IN getWishesByEventId - get all items for current wishlist");
        return result;
    }

    @Override
    public Wish getWishById(Long id) throws CustomUserServiceException {
        Wish wish = wishRepository.findWishById(id);
        if(wish==null){
            log.info("In getWishById - no items found by id: {}", id);
            throw new CustomUserServiceException("No item for such id", HttpStatus.BAD_REQUEST);
        }

        log.info("IN getWishById - get item by id", id);
        return wish;
    }
}

