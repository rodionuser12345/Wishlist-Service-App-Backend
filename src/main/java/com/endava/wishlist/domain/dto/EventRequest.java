package com.endava.wishlist.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    private static final String DEFAULT_PRIVACY = "PUBLIC";

    @JsonIgnore
    private LocalDate DEFAULT_DATE = LocalDate.now();

    @NotEmpty(message = "Title is empty!")
    private String title;

    @NotEmpty(message = "Type is empty!")
    private String type;

    @NotEmpty(message = "Description is empty!")
    private String description;

    private LocalDate date = DEFAULT_DATE;

    private String privacy = DEFAULT_PRIVACY;

}
