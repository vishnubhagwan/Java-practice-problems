package org.deliveryapp.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ride")
public class Ride {
    @GeneratedValue
    @Id
    Long id;

    @ManyToMany(mappedBy = "rides")
    Set<User> passengers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "driver_id")
    Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    Vehicle vehicle;

    @OneToOne
    @JoinColumn(name = "payment_id")
    Payment payment;

    @OneToOne
    @JoinColumn(name = "review_id")
    Review review;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "request_id", unique = true, nullable = false)
    RideRequest rideRequest;
}
