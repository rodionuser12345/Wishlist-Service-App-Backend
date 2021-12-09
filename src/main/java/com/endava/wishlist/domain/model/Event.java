package com.endava.wishlist.domain.model;

import com.endava.wishlist.domain.dto.EventRequest;
import com.endava.wishlist.domain.dto.EventResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SQLDelete(sql = "update t_events set deleted = true where id = ?")
@Where(clause = "deleted = false")
@Table(name = "t_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Title is empty!")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Type is empty!")
    @Column(name = "type")
    private String type;

    @NotEmpty(message = "Description is empty!")
    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @Column(name = "privacy")
    private String privacy;

    @Column(name = "deleted")
    private boolean deleted;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL)
    private Set<Wish> wishes;

    public EventRequest toRequestDto(){
        return EventRequest.builder()
                .type(this.getType())
                .description(this.getDescription())
                .title(this.getTitle())
                .date(this.getDate())
                .privacy(this.getPrivacy())
                .build();
    }

    public EventResponse toResponseDto(){
        return EventResponse.builder()
                .id(this.getId())
                .type(this.getType())
                .description(this.getDescription())
                .title(this.getTitle())
                .date(this.getDate())
                .privacy(this.getPrivacy())
                .build();
    }

    public Event setAll(EventRequest request){
        this.setTitle(request.getTitle());
        this.setType(request.getType());
        this.setDescription(request.getDescription());
        this.setDate(request.getDate());
        this.setPrivacy(request.getPrivacy());

        return this;
    }

    public static List<EventResponse> toDtoList(List<Event> events){
        List<EventResponse> dtoList = new ArrayList<>();
        events.forEach(e -> dtoList.add(e.toResponseDto()));
        return dtoList;
    }
}
