package com.library;

import com.library.model.Book;
import com.library.model.Member;
import com.library.service.LibraryService;
import com.library.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing Library Management System...");
        
        LibraryService libraryService = new LibraryService();

        // Seed initial data if the library is completely empty (first run)
        if (libraryService.getAllBooks().isEmpty() && libraryService.getAllMembers().isEmpty()) {
            System.out.println("Empty database detected. Seeding sample library records...");
            seedData(libraryService);
        }

        // Add a shutdown hook to ensure data is safely persisted
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown detected. Ensuring data is saved...");
            libraryService.saveData();
            System.out.println("Data saved successfully. Goodbye!");
        }));

        ConsoleUI ui = new ConsoleUI(libraryService);
        ui.start();
    }

    private static void seedData(LibraryService service) {
        // Books
        service.addBook(new Book("B001", "Effective Java", "Joshua Bloch", "Programming", 5, 5));
        service.addBook(new Book("B002", "Clean Code", "Robert C. Martin", "Software Engineering", 3, 3));
        service.addBook(new Book("B003", "Design Patterns", "Erich Gamma", "Software Engineering", 2, 2));
        service.addBook(new Book("B004", "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", 4, 4));
        service.addBook(new Book("B005", "The Pragmatic Programmer", "Andy Hunt", "Programming", 3, 3));

        // Members
        service.addMember(new Member("M001", "Alice Johnson", "alice@example.com", "555-0101", "student123", 0.0));
        service.addMember(new Member("M002", "Bob Smith", "bob@example.com", "555-0102", "student456", 15.0));
        service.addMember(new Member("M003", "Charlie Brown", "charlie@example.com", "555-0103", "student789", 0.0));

        service.saveData();
    }
}
