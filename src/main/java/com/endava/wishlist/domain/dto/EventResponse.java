package com.endava.wishlist.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class EventResponse {

    private Long id;

    private String title;

    private String type;

    private String description;

    private LocalDate date;

    private String privacy;
}
