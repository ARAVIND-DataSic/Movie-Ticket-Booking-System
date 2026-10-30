package com.movie.models.booking;
import java.time.Instant;
public class SeatLock {
    private final String userId,showId,seatId; private final Instant expiresAt;
    public SeatLock(String userId,String showId,String seatId,Instant expiresAt){this.userId=userId;this.showId=showId;this.seatId=seatId;this.expiresAt=expiresAt;}
    public String getUserId(){return userId;} public String getShowId(){return showId;} public String getSeatId(){return seatId;}
    public Instant getExpiresAt(){return expiresAt;} public boolean isExpired(){return !expiresAt.isAfter(Instant.now());}
}
