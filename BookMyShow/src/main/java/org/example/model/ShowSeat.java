package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.example.constants.SeatStatus;

import java.time.Instant;

@Data
@Entity
@Table(name = "show_seat")
public class ShowSeat {
    @Getter
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    Show show;

    Double price;

    @Enumerated(EnumType.STRING)
    SeatStatus status;

    Instant lockTime;

    @PrePersist
    protected void onCreate() {
        lockTime = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lockTime = Instant.now();
    }
}
