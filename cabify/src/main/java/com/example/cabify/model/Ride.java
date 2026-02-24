package com.example.cabify.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LINK 1: The User who booked the ride
    // "Many rides can belong to One user"
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // LINK 2: The Driver who accepted the ride
    // "Many rides can be done by One driver"
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    private String source;
    private String destination;

    private Double fare;
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private Payment payment;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}