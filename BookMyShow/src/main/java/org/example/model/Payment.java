package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.PaymentStatus;

@Data
@Entity
@Table(name = "payment")
public class Payment {
    @GeneratedValue
    @Id
    long id;

    String provider;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    @OneToOne(mappedBy = "payment")
    Booking booking;
}
