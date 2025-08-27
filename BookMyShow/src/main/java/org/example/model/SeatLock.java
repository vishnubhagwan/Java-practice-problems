package org.example.model;

import java.time.Instant;

public class SeatLock {
    long seatId;
    long userId;
    Instant lockedAt;
    Instant expiresAt;
}
