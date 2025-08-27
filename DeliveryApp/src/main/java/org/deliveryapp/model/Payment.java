package org.deliveryapp.model;

import jakarta.persistence.*;
import org.deliveryapp.constants.PaymentStatus;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    String paymentMethod;

    Double amount;

    @OneToOne(mappedBy = "payment")
    Ride ride;
}
