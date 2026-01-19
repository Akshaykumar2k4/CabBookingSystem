package com.example.cabify.repository;

import com.example.cabify.model.Ride;
import com.example.cabify.model.RideStatus;
import com.example.cabify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    // Custom method: "Find all rides where the User column matches this user"
    List<Ride> findByUser(User user);
    boolean existsByUserAndStatus(User user, RideStatus status);
}