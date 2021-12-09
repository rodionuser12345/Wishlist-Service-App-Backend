package com.endava.wishlist.domain.model;

import com.endava.wishlist.domain.dto.WishResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@SQLDelete(sql = "update t_wishes set deleted = true where id = ?")
@Where(clause = "deleted = false")
@Table(name = "t_wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Title is empty!")
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "link")
    private String link;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "priority")
    private String priority;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public WishResponse toWishResponse() {
        return WishResponse.builder()
                .id(this.getId())
                .title(this.getTitle())
                .description(this.getDescription())
                .image(this.getImage())
                .link(this.getLink())
                .price(this.getPrice())
                .currency(this.getCurrency())
                .priority(this.getPriority())
                .build();
    }
}
