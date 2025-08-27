package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "booking_item")
public class BookingItem {
    @GeneratedValue
    @Id
    long id;

    @OneToOne
    @JoinColumn(name = "seat_id")
    ShowSeat showSeat;

    Double price;
}
