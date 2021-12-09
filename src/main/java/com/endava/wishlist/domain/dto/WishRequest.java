package com.endava.wishlist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishRequest {

    private static final String DEFAULT_CURRENCY = "MDL";

    private static final String DEFAULT_PRIORITY = "Want to have";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("0.00");

    @NotEmpty(message = "Title is empty!")
    private String title;

    private String description;

    private String image;

    private String link ;

    private BigDecimal price = DEFAULT_PRICE;

    private String currency = DEFAULT_CURRENCY;

    private String priority = DEFAULT_PRIORITY;
}
