package org.deliveryapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.deliveryapp.constants.UserType;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {
    @GeneratedValue
    @Id
    Long id;

    String name;

    String email;

    String phone;

    @Enumerated(EnumType.STRING)
    UserType userType;

    Double rating;

    @ManyToMany
    @JoinTable(
            name = "user_rides",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ride_id")
    )
    Set<Ride> rides = new HashSet<>();
}
