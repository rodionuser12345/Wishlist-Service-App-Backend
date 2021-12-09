package com.endava.wishlist.web.controller;

import com.endava.wishlist.domain.dto.EventRequest;
import com.endava.wishlist.domain.dto.EventResponse;
import com.endava.wishlist.domain.model.Event;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.service.EventService;
import com.endava.wishlist.web.security.jwt.JwtProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Data
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishListsController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/{email}/wishlist")
    public ResponseEntity<String> addWishlists(@PathVariable(value = "email") String email,
                                               @Valid @RequestBody EventRequest eventRequest) throws CustomUserServiceException {
        eventService.saveEvent(eventRequest, email);
        return new ResponseEntity<>("Save", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteWishList(@PathVariable("id") Long id) throws CustomUserServiceException {
        eventService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/wishlist/{id}")
    public ResponseEntity<EventRequest> getWishListById(@PathVariable("id") Long id) throws CustomUserServiceException {
        return ResponseEntity.ok(eventService.findById(id).toRequestDto());
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllWishes() {
        return ResponseEntity.ok(Event.toDtoList(eventService.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventRequest> changeWishListById(@PathVariable("id") Long id, @Valid @RequestBody EventRequest request) throws CustomUserServiceException{
        return ResponseEntity.ok(eventService.changeById(id, request).toRequestDto());
    }

}



