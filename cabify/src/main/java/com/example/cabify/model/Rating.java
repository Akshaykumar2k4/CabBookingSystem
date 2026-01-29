package com.example.cabify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId; // [cite: 55, 64]

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride; // [cite: 55, 64]

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId; // [cite: 55, 64]

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId; // [cite: 55]

    @Column(nullable = false)
    private Integer score; // [cite: 55]

    private String comments; // [cite: 55]

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // This method runs automatically before the record is inserted
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}