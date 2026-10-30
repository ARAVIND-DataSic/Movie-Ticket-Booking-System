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
