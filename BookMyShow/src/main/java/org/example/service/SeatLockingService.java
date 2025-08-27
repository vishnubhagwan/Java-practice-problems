package org.example.service;

import org.example.model.ShowSeat;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SeatLockingService {
    Map<String, Boolean> seatMap;

    SeatLockingService() {
        seatMap = new HashMap<>();
    }

    synchronized boolean tryLockSeat(ShowSeat seat) {
        String key = String.format("%s-%s", seat.getShow().getId(), seat.getSeat().getId());
        if(seatMap.containsKey(key))
            return false;
        seatMap.put(key, true);
        return true;
    }

    synchronized void releaseLock(ShowSeat seat ) {
        String key = String.format("%s-%s", seat.getShow().getId(), seat.getSeat().getId());
        seatMap.remove(key);
    }
}
