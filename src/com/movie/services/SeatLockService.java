package com.movie.services;
import com.movie.models.booking.SeatLock;
import com.movie.repository.BookingRepository;
import com.movie.repository.SeatLockRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
public class SeatLockService {
    private final SeatLockRepository locks;
    private final BookingRepository bookings;
    private final Duration duration;
    public SeatLockService(SeatLockRepository locks,BookingRepository bookings) { this(locks,bookings,Duration.ofMinutes(5)); }
    public SeatLockService(SeatLockRepository locks,BookingRepository bookings,Duration duration) { this.locks=locks; this.bookings=bookings; this.duration=duration; }
    public synchronized List<SeatLock> lockSeats(String userId,String showId,List<String> seatIds) {
        if (userId==null || userId.isBlank() || showId==null || showId.isBlank()) throw new IllegalArgumentException("User and show are required");
        if (seatIds==null || seatIds.isEmpty() || new HashSet<>(seatIds).size()!=seatIds.size()) throw new IllegalArgumentException("Select unique seats");
        Instant now=Instant.now();
        for(String seat:seatIds) {
            if(seat==null || seat.isBlank()) throw new IllegalArgumentException("Seat ID is required");
            if(bookings.isSeatBooked(showId,seat)) throw new IllegalStateException("Seat already booked: "+seat);
            Optional<SeatLock> existing=locks.find(showId,seat);
            if(existing.isPresent() && !existing.get().isExpired() && !existing.get().getUserId().equals(userId)) throw new IllegalStateException("Seat is locked: "+seat);
        }
        List<SeatLock> result=new ArrayList<>();
        for(String seat:seatIds) { SeatLock lock=new SeatLock(userId,showId,seat,now.plus(duration)); locks.save(lock); result.add(lock); }
        return result;
    }
    public synchronized void release(String userId,String showId,List<String> seatIds) {
        for(String seat:seatIds) locks.find(showId,seat).filter(l -> l.getUserId().equals(userId)).ifPresent(l -> locks.delete(showId,seat));
    }
    public synchronized void consume(String userId,String showId,List<String> seatIds) {
        for(String seat:seatIds) {
            SeatLock lock=locks.find(showId,seat).orElseThrow(() -> new IllegalStateException("Seat lock expired or missing: "+seat));
            if(lock.isExpired() || !lock.getUserId().equals(userId)) throw new IllegalStateException("Seat lock expired or belongs to another user: "+seat);
        }
        release(userId,showId,seatIds);
    }
}
