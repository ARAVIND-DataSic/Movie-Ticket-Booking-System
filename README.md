# Movie Ticket Booking System

A Java-based Movie Ticket Booking System designed using Object-Oriented Programming (OOP) and Low-Level Design (LLD) principles.

## Project Overview

This project implements the initial Java package structure for a movie ticket booking system.

## Source Layout

Java source files are organized under `src/com/movie`.

### Compile and Run

Execute the following commands from the project root using PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.movie.Main
```

## Project Modules

- Model
- CLI
- Repository
- Service
- Payment

The model, CLI, repository, service, and payment components currently contain placeholders and are ready for implementation.

## Technologies

- Java
- Object-Oriented Programming
- Low-Level Design
- Git and GitHub