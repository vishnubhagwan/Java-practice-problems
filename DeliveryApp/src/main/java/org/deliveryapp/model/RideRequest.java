package org.deliveryapp.model;

import jakarta.persistence.*;
import org.deliveryapp.constants.RideStatus;

@Entity
@Table(name = "ride_request")
public class RideRequest {
    @GeneratedValue
    @Id
    Long id;

    @OneToOne
    @JoinColumn(name = "passenger_id")
    User passenger;

    String pickupLocation;

    String dropOffLocation;

    Long requestTime;

    @Enumerated(EnumType.STRING)
    RideStatus status;
}
