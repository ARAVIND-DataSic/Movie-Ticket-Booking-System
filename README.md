# Movie Ticket Booking System

This project contains the Java package structure for a movie ticket booking system, including Module 4 payment, offer, and refund services.

## Source layout

Java source files are under `src/com/movie`. Compile and run the current scaffold from the project root:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.movie.Main
```

## Module 4

Payment processing is simulated; do not enter real payment credentials. Card payments accept a simulated authorization token, and each payment method returns an authorization result through the shared `com.movie.payment.PaymentMethod` interface. Payment totals use `BigDecimal`:

```text
seat prices + ₹20 per ticket + 18% GST on convenience fee - offer discount
```

In-memory repositories store payments, offers, offer redemptions, and refunds. `BookingPaymentGateway` and `BookingRefundGateway` are the integration ports for Member 4's Booking and SeatLock services. The Booking module should provide booking confirmation/failure/seat-release operations and expose refundable booking details including booking/payment IDs, showtime, refundable ticket amount, total amount, and active/cancellable state.

Run the dependency-free Module 4 scenario tests from the project root:

```powershell
$sources = Get-ChildItem -Recurse -File src,test -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $sources
java -cp out com.movie.services.Module4Tests
```
