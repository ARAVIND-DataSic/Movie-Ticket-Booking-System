# Movie Ticket Booking System

This project contains a Java movie ticket booking system. Movie, theatre, screen, and seat
management is implemented with validated models, service-layer rules, in-memory repositories,
and an interactive console interface.

## Source layout

Java source files are under `src/com/movie`. Compile and run from the project root:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.movie.Main
```

From the main menu, choose movie management or theatre, screen, and seat management. Records
are held in memory and are cleared when the application exits. Repository interfaces allow
the in-memory implementations to be replaced with persistent storage later.

Movie genres, certificates, and seat types are entered using their enum names (for example,
`SCI_FI`, `UA`, or `RECLINER`). Release dates use `YYYY-MM-DD`.
