package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.BookingStatus;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "booking")
public class Booking {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    User user;

    UUID ref;

    @Enumerated(value = EnumType.STRING)
    BookingStatus status;

    Double totalAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<BookingItem> bookingItems;

    @OneToOne
    @JoinColumn(name = "payment_id")
    Payment payment;
}
