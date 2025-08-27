package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @GeneratedValue
    @Id
    Long Id;

    String name;

    @OneToMany(mappedBy = "user")
    List<Booking> bookings;
}
