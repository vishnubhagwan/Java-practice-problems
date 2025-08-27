package org.deliveryapp.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "driver")
public class Driver {
    @GeneratedValue
    @Id
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    Vehicle vehicle;

    String licenceNo;

    Double rating;

    Integer expYears;

    @OneToMany(mappedBy = "driver")
    Set<Ride> rides = new HashSet<>();
}
