package com.endava.wishlist.repository;

import com.endava.wishlist.domain.model.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
    Optional<Wish> findByTitle(String title);

    List<Wish> findWishesByEventId(Long id);

    Wish findWishById(Long id);
}
