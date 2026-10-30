package com.movie.repository;
import com.movie.models.booking.SeatLock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public interface SeatLockRepository {
    Optional<SeatLock> find(String showId, String seatId);
    void save(SeatLock lock);
    void delete(String showId, String seatId);
    class InMemory implements SeatLockRepository {
        private final Map<String,SeatLock> locks = new ConcurrentHashMap<>();
        private String key(String showId,String seatId) { return showId + "::" + seatId; }
        public Optional<SeatLock> find(String showId,String seatId) { return Optional.ofNullable(locks.get(key(showId,seatId))); }
        public void save(SeatLock lock) { locks.put(key(lock.getShowId(),lock.getSeatId()),lock); }
        public void delete(String showId,String seatId) { locks.remove(key(showId,seatId)); }
    }
}
