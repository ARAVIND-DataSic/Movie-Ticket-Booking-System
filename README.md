# Movie Ticket Booking System

This project contains the initial Java package structure for a movie ticket booking system.

## Source layout

Java source files are under `src/com/movie`. Compile and run the current scaffold from the project root:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.movie.Main
```

The model, CLI, repository, service, and payment types are placeholders ready for implementation.
