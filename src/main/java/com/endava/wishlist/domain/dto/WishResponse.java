package com.endava.wishlist.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WishResponse {
    private Long id;
    private String title;
    private String description;
    private String image;
    private String link ;
    private BigDecimal price;
    private String currency;
    private String priority;

}
