# Movie Ticket Booking System

A Java-based movie ticket booking system designed using object-oriented programming and
low-level design principles.

## Project Overview

Java source files are organized under `src/com/movie`. Compile and run from the project root
using PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.movie.Main
```

The console application includes movie, theatre, screen, and seat management. These features
use validated models, service-layer rules, and repository interfaces backed by in-memory
storage. Records are cleared when the application exits.

From the main menu, choose movie management or theatre, screen, and seat management. Movie
genres, certificates, and seat types are entered using their enum names (for example,
`SCI_FI`, `UA`, or `RECLINER`). Release dates use `YYYY-MM-DD`.

## Project Modules

- Models
- CLI
- Repositories
- Services
- Payment

## Technologies

- Java
- Object-oriented programming
- Low-level design
- Git and GitHub

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
