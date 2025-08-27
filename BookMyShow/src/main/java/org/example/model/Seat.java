package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import org.example.constants.SeatStatus;
import org.example.constants.SeatType;

@Data
@Entity
@Table(name = "seat")
public class Seat {
    @GeneratedValue
    @Id
    long id;

    String row;

    int col;

    SeatType seatType;

    Double basePrice;

    SeatStatus seatStatus;

    int version;
}
