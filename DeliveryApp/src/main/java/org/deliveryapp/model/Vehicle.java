package org.deliveryapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Setter;
import org.deliveryapp.constants.VehicleType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @GeneratedValue
    @Id
    @Setter(AccessLevel.NONE)
    Long id;

    String number;

    @Enumerated(EnumType.STRING)
    VehicleType type;

    Integer capacity;

    @OneToOne(mappedBy = "vehicle")
    @JoinColumn(name = "driver_id")
    Driver driver;

    @OneToMany(mappedBy = "vehicle")
    Set<Ride> rides = new HashSet<>();
}
