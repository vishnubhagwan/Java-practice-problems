package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.ShowStatus;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "show")
public class Show {
    @GeneratedValue
    @Id
    Long id;

    Date startTime;

    Date endtime;

    Integer basePrice;

    ShowStatus status;

    @OneToMany(mappedBy = "show", fetch = FetchType.LAZY)
    List<ShowSeat> seats;
}
