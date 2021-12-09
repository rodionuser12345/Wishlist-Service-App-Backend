package com.endava.wishlist.repository;

import com.endava.wishlist.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM t_events WHERE user_id = (SELECT id FROM t_users WHERE email = ?1) AND deleted = false", nativeQuery = true)
    List<Event> findAllByEmail(String emailAddress);

    Optional<Event> findByTitle(String title);

    @Query(value = "SELECT email FROM t_users WHERE id = (SELECT user_id FROM t_events WHERE id = ?1)", nativeQuery = true)
    String findOwner(Long id);

    @Query(value = "SELECT privacy FROM t_events WHERE id = ?1", nativeQuery = true)
    String checkPrivacy(Long id);
}
