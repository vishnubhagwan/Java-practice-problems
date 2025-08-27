package org.deliveryapp.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "review")
public class Review {
    @GeneratedValue
    @Id
    Long id;

    @OneToOne
    @JoinColumn(name = "reviewer_id")
    User user;

    @OneToOne
    @JoinColumn(name = "ride_id")
    Ride ride;

    Double rating;

    String comment;

    Instant reviewTime;
}
