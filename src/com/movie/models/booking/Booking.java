package com.movie.models.booking;
import java.time.Instant;
import java.util.*;
public class Booking {
    private final String id=UUID.randomUUID().toString(), userId, showId;
    private final List<String> seatIds;
    private final Instant createdAt=Instant.now();
    private BookingStatus status=BookingStatus.CONFIRMED;
    public Booking(String userId,String showId,List<String> seatIds) { this.userId=userId; this.showId=showId; this.seatIds=List.copyOf(seatIds); }
    public String getId(){return id;} public String getUserId(){return userId;} public String getShowId(){return showId;}
    public List<String> getSeatIds(){return Collections.unmodifiableList(seatIds);} public Instant getCreatedAt(){return createdAt;}
    public BookingStatus getStatus(){return status;} public void cancel(){status=BookingStatus.CANCELLED;}
}
