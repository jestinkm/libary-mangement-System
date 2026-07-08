package com.library.ui;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.IssueRecord;
import com.library.service.LibraryService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ConsoleUI {
    private final LibraryService libraryService;
    private final Scanner scanner;
    private LocalDate systemDate;

    // ANSI Escape Codes for Styling
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String PURPLE = "\u001B[35m";

    // Session State
    private boolean isAdminLoggedIn = false;
    private Member loggedInMember = null;

    public ConsoleUI(LibraryService libraryService) {
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
        this.systemDate = LocalDate.now(); // Defaults to today
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int choice = readIntegerInput("Enter your choice: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1:
                        handleLoginFlow();
                        break;
                    case 2:
                        if (checkAdminAccess()) {
                            handleBookManagementMenu();
                        }
                        break;
                    case 3:
                        if (checkAdminAccess()) {
                            handleMemberManagementMenu();
                        }
                        break;
                    case 4:
                        if (checkAdminAccess()) {
                            handleIssueBookFlow();
                        }
                        break;
                    case 5:
                        if (checkAdminAccess()) {
                            handleReturnBookFlow();
                        }
                        break;
                    case 6:
                        handleSearchBookFlow();
                        break;
                    case 7:
                        handleViewAvailableBooks();
                        break;
                    case 8:
                        handleViewIssuedBooks();
                        break;
                    case 9:
                        if (checkAdminAccess()) {
                            handleReportsMenu();
                        }
                        break;
                    case 10:
                        exit = handleExitFlow();
                        break;
                    default:
                        printError("Invalid choice! Please choose between 1 and 10.");
                }
            } catch (Exception e) {
                printError("An unexpected error occurred: " + e.getMessage());
            }
            System.out.println();
        }
    }

    // ==========================================
    // Menus
    // ==========================================
    private void printMainMenu() {
        System.out.println(CYAN + BOLD + "=============================================" + RESET);
        System.out.println(CYAN + BOLD + "========= Library Management System =========" + RESET);
        System.out.println(CYAN + BOLD + "=============================================" + RESET);
        
        // Show session status
        if (isAdminLoggedIn) {
            System.out.println(GREEN + "Logged in as: " + BOLD + "ADMIN" + RESET + " | System Date: " + PURPLE + systemDate + RESET);
        } else if (loggedInMember != null) {
            System.out.println(GREEN + "Logged in as: " + BOLD + loggedInMember.getName() + " (" + loggedInMember.getId() + ")" + RESET + " | System Date: " + PURPLE + systemDate + RESET);
        } else {
            System.out.println(YELLOW + "Status: Guest/Not Logged In" + RESET + " | System Date: " + PURPLE + systemDate + RESET);
        }
        System.out.println();
        System.out.println("1. Login / Switch Role");
        System.out.println("2. Book Management");
        System.out.println("3. Member Management");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. Search Book");
        System.out.println("7. View Available Books");
        System.out.println("8. View Issued Books");
        System.out.println("9. Reports");
        System.out.println("10. Exit / Save");
        System.out.println(CYAN + "---------------------------------------------" + RESET);
    }

    private void handleBookManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println(BLUE + BOLD + "------ Book Management ------" + RESET);
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. View Books");
            System.out.println("5. Search Book");
            System.out.println("6. Back");
            System.out.println(BLUE + "-----------------------------" + RESET);
            
            int choice = readIntegerInput("Enter your choice: ");
            System.out.println();
            switch (choice) {
                case 1:
                    handleAddBook();
                    break;
                case 2:
                    handleUpdateBook();
                    break;
                case 3:
                    handleDeleteBook();
                    break;
                case 4:
                    handleViewAllBooks();
                    break;
                case 5:
                    handleSearchBookFlow();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    printError("Invalid choice! Choose between 1 and 6.");
            }
            System.out.println();
        }
    }

    private void handleMemberManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println(BLUE + BOLD + "------ Member Management ------" + RESET);
            System.out.println("1. Add Member");
            System.out.println("2. Update Member");
            System.out.println("3. Delete Member");
            System.out.println("4. View Members");
            System.out.println("5. Back");
            System.out.println(BLUE + "-------------------------------" + RESET);
            
            int choice = readIntegerInput("Enter your choice: ");
            System.out.println();
            switch (choice) {
                case 1:
                    handleAddMember();
                    break;
                case 2:
                    handleUpdateMember();
                    break;
                case 3:
                    handleDeleteMember();
                    break;
                case 4:
                    handleViewMembers();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    printError("Invalid choice! Choose between 1 and 5.");
            }
            System.out.println();
        }
    }

    private void handleReportsMenu() {
        Map<String, Object> data = libraryService.getReportsData(systemDate);
        System.out.println(PURPLE + BOLD + "------ Reports ------" + RESET);
        System.out.printf("Total Books       : %d\n", data.get("totalBooks"));
        System.out.printf("Available Books   : %d\n", data.get("availableBooks"));
        System.out.printf("Issued Books      : %d\n", data.get("activeIssues"));
        System.out.printf("Total Members     : %d\n", data.get("totalMembers"));
        System.out.printf("Overdue Books     : %d\n", data.get("overdueBooks"));
        System.out.printf("Unpaid Fines      : $%.2f\n", data.get("totalPendingFines"));
        System.out.println(PURPLE + "---------------------" + RESET);
        
        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    // ==========================================
    // Login Flow
    // ==========================================
    private void handleLoginFlow() {
        System.out.println(CYAN + BOLD + "------ Login Select ------" + RESET);
        System.out.println("1. Admin Login");
        System.out.println("2. Student/Member Login");
        System.out.println("3. Register New Student Account");
        System.out.println("4. Log Out Current User");
        System.out.println("5. Back");
        int type = readIntegerInput("Enter choice: ");
        System.out.println();
        
        switch (type) {
            case 1:
                System.out.print("Enter Admin Username: ");
                String adminUser = scanner.nextLine().trim();
                System.out.print("Enter Admin Password: ");
                String adminPass = scanner.nextLine().trim();
                
                if (libraryService.authenticateAdmin(adminUser, adminPass)) {
                    isAdminLoggedIn = true;
                    loggedInMember = null;
                    printSuccess("Admin logged in successfully!");
                } else {
                    printError("Invalid credentials!");
                }
                break;
            case 2:
                System.out.print("Enter Member ID: ");
                String memberId = scanner.nextLine().trim();
                System.out.print("Enter Password: ");
                String memberPass = scanner.nextLine().trim();
                
                Member member = libraryService.authenticateMember(memberId, memberPass);
                if (member != null) {
                    loggedInMember = member;
                    isAdminLoggedIn = false;
                    printSuccess("Welcome back, " + member.getName() + "!");
                    handleStudentPortal(member);
                } else {
                    printError("Invalid Member ID or Password!");
                }
                break;
            case 3:
                handleStudentRegistration();
                break;
            case 4:
                isAdminLoggedIn = false;
                loggedInMember = null;
                printSuccess("Logged out successfully.");
                break;
            case 5:
                break;
            default:
                printError("Invalid option.");
        }
    }

    private void handleStudentRegistration() {
        System.out.println(BLUE + BOLD + "------ Student Registration ------" + RESET);
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            printError("Name cannot be empty.");
            return;
        }
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Set Login Password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            printError("Password cannot be empty.");
            return;
        }

        // Auto-generate unique student ID (MXXX)
        int nextNum = libraryService.getAllMembers().size() + 1;
        String id = "M" + String.format("%03d", nextNum);
        
        while (libraryService.getMember(id) != null) {
            nextNum++;
            id = "M" + String.format("%03d", nextNum);
        }

        Member member = new Member(id, name, email, phone, password, 0.0);
        if (libraryService.addMember(member)) {
            printSuccess("Registration successful! Your Member ID is: " + BOLD + id + RESET);
            System.out.println("You can now log in using ID: " + id + " and your password.");
        } else {
            printError("Registration failed.");
        }
    }

    private void handleStudentPortal(Member member) {
        boolean exitPortal = false;
        while (exitPortal && loggedInMember != null && loggedInMember.getId().equals(member.getId())) {
            // Wait, we can implement a custom screen or loop. Let's make it simple:
            // Since they are logged in, they can use the main menu or a simple sub-portal.
            // Let's offer a dedicated Student Portal right away!
            System.out.println(GREEN + BOLD + "------ Student Portal (" + member.getName() + ") ------" + RESET);
            System.out.println("1. View Available Books");
            System.out.println("2. Search Book");
            System.out.println("3. View My Issued Books & Active Fines");
            System.out.println("4. Pay Outstanding Fine");
            System.out.println("5. Back to Main Menu");
            int choice = readIntegerInput("Enter choice: ");
            System.out.println();
            switch (choice) {
                case 1:
                    handleViewAvailableBooks();
                    break;
                case 2:
                    handleSearchBookFlow();
                    break;
                case 3:
                    viewMemberDashboard(member.getId());
                    break;
                case 4:
                    handlePayFineFlow(member.getId());
                    break;
                case 5:
                    exitPortal = true;
                    break;
                default:
                    printError("Invalid choice.");
            }
            System.out.println();
        }
        // Actually, let's keep them in the main menu loop, but if they are student,
        // we can customize behavior. Let's let them access options from main menu too!
        // To be safe, let's offer a Student Portal menu when they login:
        if (loggedInMember != null) {
            runStudentPortalLoop();
        }
    }

    private void runStudentPortalLoop() {
        while (loggedInMember != null) {
            System.out.println(GREEN + BOLD + "------ Student Portal (" + loggedInMember.getName() + ") ------" + RESET);
            System.out.println("1. View Available Books");
            System.out.println("2. Search Book");
            System.out.println("3. View My Issued Books & Active Fines");
            System.out.println("4. Pay Outstanding Fine");
            System.out.println("5. Log Out");
            int choice = readIntegerInput("Enter choice: ");
            System.out.println();
            switch (choice) {
                case 1:
                    handleViewAvailableBooks();
                    break;
                case 2:
                    handleSearchBookFlow();
                    break;
                case 3:
                    viewMemberDashboard(loggedInMember.getId());
                    break;
                case 4:
                    handlePayFineFlow(loggedInMember.getId());
                    break;
                case 5:
                    loggedInMember = null;
                    printSuccess("Logged out from Student Portal.");
                    break;
                default:
                    printError("Invalid choice.");
            }
            System.out.println();
        }
    }

    private boolean checkAdminAccess() {
        if (!isAdminLoggedIn) {
            printWarning("Access Denied: Admin privileges required.");
            System.out.print("Would you like to log in as Admin now? (y/n): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (ans.equals("y") || ans.equals("yes")) {
                System.out.print("Enter Admin Username: ");
                String adminUser = scanner.nextLine().trim();
                System.out.print("Enter Admin Password: ");
                String adminPass = scanner.nextLine().trim();
                if (libraryService.authenticateAdmin(adminUser, adminPass)) {
                    isAdminLoggedIn = true;
                    loggedInMember = null;
                    printSuccess("Admin logged in successfully!");
                    return true;
                } else {
                    printError("Invalid credentials!");
                }
            }
            return false;
        }
        return true;
    }

    // ==========================================
    // Book Operations
    // ==========================================
    private void handleAddBook() {
        System.out.println(BLUE + "Add New Book" + RESET);
        System.out.print("Enter Book ID (e.g. B001): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            printError("Book ID cannot be empty.");
            return;
        }
        if (libraryService.getBook(id) != null) {
            printError("A book with ID " + id + " already exists.");
            return;
        }
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();
        int totalCopies = readIntegerInput("Enter Total Copies: ");
        if (totalCopies <= 0) {
            printError("Total copies must be positive.");
            return;
        }

        Book book = new Book(id, title, author, category, totalCopies, totalCopies);
        if (libraryService.addBook(book)) {
            printSuccess("Book added successfully!");
        } else {
            printError("Failed to add book.");
        }
    }

    private void handleUpdateBook() {
        System.out.println(BLUE + "Update Book" + RESET);
        System.out.print("Enter Book ID to update: ");
        String id = scanner.nextLine().trim();
        Book book = libraryService.getBook(id);
        if (book == null) {
            printError("Book not found!");
            return;
        }
        System.out.println("Current details: " + book);
        System.out.print("Enter New Title (leave blank to keep current): ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) title = book.getTitle();

        System.out.print("Enter New Author (leave blank to keep current): ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) author = book.getAuthor();

        System.out.print("Enter New Category (leave blank to keep current): ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) category = book.getCategory();

        System.out.print("Enter New Total Copies (current: " + book.getTotalCopies() + ", input -1 to keep current): ");
        int totalCopies = readIntegerInput("");
        if (totalCopies == -1) {
            totalCopies = book.getTotalCopies();
        } else if (totalCopies < 0) {
            printError("Copies cannot be negative.");
            return;
        }

        if (libraryService.updateBook(id, title, author, category, totalCopies)) {
            printSuccess("Book updated successfully!");
        } else {
            printError("Failed to update book.");
        }
    }

    private void handleDeleteBook() {
        System.out.println(BLUE + "Delete Book" + RESET);
        System.out.print("Enter Book ID to delete: ");
        String id = scanner.nextLine().trim();
        if (libraryService.deleteBook(id)) {
            printSuccess("Book deleted successfully!");
        } else {
            printError("Failed to delete book. Ensure it exists and has no active borrow records.");
        }
    }

    private void handleViewAllBooks() {
        System.out.println(BLUE + "All Books" + RESET);
        Collection<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        books.forEach(System.out::println);
    }

    private void handleSearchBookFlow() {
        System.out.println(BLUE + "Search Books" + RESET);
        System.out.println("Search by:");
        System.out.println("1. Book ID");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.println("4. Category");
        int typeChoice = readIntegerInput("Enter option: ");
        
        String type;
        switch (typeChoice) {
            case 1: type = "id"; break;
            case 2: type = "title"; break;
            case 3: type = "author"; break;
            case 4: type = "category"; break;
            default:
                printError("Invalid option.");
                return;
        }

        System.out.print("Enter search keyword: ");
        String query = scanner.nextLine().trim();
        List<Book> results = libraryService.searchBooks(query, type);
        System.out.println("\nSearch Results:");
        if (results.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    // ==========================================
    // Member Operations
    // ==========================================
    private void handleAddMember() {
        System.out.println(BLUE + "Add New Member" + RESET);
        System.out.print("Enter Member ID (e.g. M001): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            printError("Member ID cannot be empty.");
            return;
        }
        if (libraryService.getMember(id) != null) {
            printError("A member with ID " + id + " already exists.");
            return;
        }
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Set Login Password: ");
        String password = scanner.nextLine().trim();

        Member member = new Member(id, name, email, phone, password, 0.0);
        if (libraryService.addMember(member)) {
            printSuccess("Member registered successfully!");
        } else {
            printError("Failed to add member.");
        }
    }

    private void handleUpdateMember() {
        System.out.println(BLUE + "Update Member" + RESET);
        System.out.print("Enter Member ID to update: ");
        String id = scanner.nextLine().trim();
        Member member = libraryService.getMember(id);
        if (member == null) {
            printError("Member not found!");
            return;
        }
        System.out.println("Current details: " + member);
        System.out.print("Enter New Name (leave blank to keep current): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = member.getName();

        System.out.print("Enter New Email (leave blank to keep current): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = member.getEmail();

        System.out.print("Enter New Phone (leave blank to keep current): ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) phone = member.getPhone();

        System.out.print("Enter New Password (leave blank to keep current): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) password = member.getPassword();

        if (libraryService.updateMember(id, name, email, phone, password)) {
            printSuccess("Member updated successfully!");
        } else {
            printError("Failed to update member.");
        }
    }

    private void handleDeleteMember() {
        System.out.println(BLUE + "Delete Member" + RESET);
        System.out.print("Enter Member ID to delete: ");
        String id = scanner.nextLine().trim();
        if (libraryService.deleteMember(id)) {
            printSuccess("Member deleted successfully!");
        } else {
            printError("Failed to delete member. Make sure they have no outstanding borrow records or unpaid fines.");
        }
    }

    private void handleViewMembers() {
        System.out.println(BLUE + "All Registered Members" + RESET);
        Collection<Member> members = libraryService.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        members.forEach(System.out::println);
    }

    // ==========================================
    // Borrow & Return Operations
    // ==========================================
    private void handleIssueBookFlow() {
        System.out.println(BLUE + "Issue Book Wizard" + RESET);
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine().trim();

        // Support custom issue date for time travel testing
        System.out.println("Select Issue Date Option:");
        System.out.println("1. Use Current System Date (" + systemDate + ")");
        System.out.println("2. Input Custom Date (YYYY-MM-DD)");
        int dateChoice = readIntegerInput("Choice: ");
        
        LocalDate issueDate = systemDate;
        if (dateChoice == 2) {
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String inputDateStr = scanner.nextLine().trim();
            try {
                issueDate = LocalDate.parse(inputDateStr);
            } catch (DateTimeParseException e) {
                printError("Invalid date format! Falling back to system date.");
            }
        }

        try {
            String issueId = libraryService.issueBook(bookId, memberId, issueDate);
            printSuccess("Book successfully issued!");
            System.out.println("Issue Record Details:");
            System.out.println(libraryService.getIssueRecord(issueId));
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void handleReturnBookFlow() {
        System.out.println(BLUE + "Return Book Wizard" + RESET);
        System.out.print("Enter Issue Record ID (e.g. I001): ");
        String issueId = scanner.nextLine().trim();

        IssueRecord record = libraryService.getIssueRecord(issueId);
        if (record == null) {
            printError("Issue record not found!");
            return;
        }

        System.out.println("Record Found: " + record);
        
        // Date choice for return (for time travel testing)
        System.out.println("Select Return Date Option:");
        System.out.println("1. Use Current System Date (" + systemDate + ")");
        System.out.println("2. Input Custom Date (YYYY-MM-DD)");
        int dateChoice = readIntegerInput("Choice: ");
        
        LocalDate returnDate = systemDate;
        if (dateChoice == 2) {
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String inputDateStr = scanner.nextLine().trim();
            try {
                returnDate = LocalDate.parse(inputDateStr);
            } catch (DateTimeParseException e) {
                printError("Invalid date format! Falling back to system date.");
            }
        }

        try {
            double fine = libraryService.returnBook(issueId, returnDate);
            printSuccess("Book successfully returned!");
            if (fine > 0) {
                printWarning(String.format("Late return fine generated: $%.2f", fine));
                Member m = libraryService.getMember(record.getMemberId());
                if (m != null) {
                    System.out.println("New total fine balance for " + m.getName() + ": $" + String.format("%.2f", m.getFineBalance()));
                }
            } else {
                printSuccess("Returned on time. No fines generated.");
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // ==========================================
    // Book Availability Views
    // ==========================================
    private void handleViewAvailableBooks() {
        System.out.println(BLUE + "Available Books (In Stock)" + RESET);
        List<Book> avail = libraryService.getAvailableBooks();
        if (avail.isEmpty()) {
            System.out.println("No books are currently in stock.");
        } else {
            avail.forEach(System.out::println);
        }
    }

    private void handleViewIssuedBooks() {
        System.out.println(BLUE + "Issued Books (Currently Checked Out)" + RESET);
        
        // Show all active issue records
        Collection<IssueRecord> allIssues = libraryService.getAllIssues();
        long activeCount = allIssues.stream().filter(r -> !r.isReturned()).count();
        
        if (activeCount == 0) {
            System.out.println("No books are currently issued out.");
            return;
        }

        System.out.println("Active Issues:");
        for (IssueRecord r : allIssues) {
            if (!r.isReturned()) {
                // Calculate dynamic fine if overdue relative to systemDate
                double dynamicFine = r.calculateFine(systemDate);
                System.out.print(r);
                if (dynamicFine > 0) {
                    System.out.print(RED + " (OVERDUE - Current Est. Fine: $" + String.format("%.2f", dynamicFine) + ")" + RESET);
                }
                System.out.println();
            }
        }
    }

    // ==========================================
    // Member Dashboard & Fines
    // ==========================================
    private void viewMemberDashboard(String memberId) {
        Member m = libraryService.getMember(memberId);
        if (m == null) return;

        System.out.println(GREEN + "Dashboard for " + m.getName() + " (" + m.getId() + ")" + RESET);
        System.out.println("Email: " + m.getEmail() + " | Phone: " + m.getPhone());
        System.out.println("Outstanding Fine Balance: $" + String.format("%.2f", m.getFineBalance()));
        System.out.println();
        
        List<IssueRecord> records = libraryService.getMemberAllIssues(memberId);
        if (records.isEmpty()) {
            System.out.println("No borrowing history found.");
        } else {
            System.out.println("Borrowing History:");
            for (IssueRecord r : records) {
                System.out.print(r);
                if (!r.isReturned()) {
                    double estFine = r.calculateFine(systemDate);
                    if (estFine > 0) {
                        System.out.print(RED + " (OVERDUE - Est Fine: $" + String.format("%.2f", estFine) + ")" + RESET);
                    }
                }
                System.out.println();
            }
        }
    }

    private void handlePayFineFlow(String memberId) {
        Member m = libraryService.getMember(memberId);
        if (m == null) return;

        System.out.println("Outstanding Fine Balance: $" + String.format("%.2f", m.getFineBalance()));
        if (m.getFineBalance() <= 0) {
            printSuccess("You have no pending fines!");
            return;
        }

        System.out.print("Enter amount to pay: ");
        double amount = readDoubleInput();
        try {
            double newBalance = libraryService.payMemberFine(memberId, amount);
            printSuccess(String.format("Payment of $%.2f successful!", amount));
            System.out.println("New Balance: $" + String.format("%.2f", newBalance));
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // ==========================================
    // Exit Logic
    // ==========================================
    private boolean handleExitFlow() {
        System.out.print("Are you sure you want to exit? Data will be saved automatically. (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            libraryService.saveData();
            printSuccess("Data saved successfully. Goodbye!");
            return true;
        }
        return false;
    }

    // ==========================================
    // Helpers & Validations
    // ==========================================
    private int readIntegerInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return -1;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Invalid input! Please enter a valid integer.");
            }
        }
    }

    private double readDoubleInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                printError("Invalid input! Please enter a valid decimal number: ");
            }
        }
    }

    private void printSuccess(String msg) {
        System.out.println(GREEN + BOLD + "[SUCCESS] " + RESET + GREEN + msg + RESET);
    }

    private void printWarning(String msg) {
        System.out.println(YELLOW + BOLD + "[WARNING] " + RESET + YELLOW + msg + RESET);
    }

    private void printError(String msg) {
        System.out.println(RED + BOLD + "[ERROR] " + RESET + RED + msg + RESET);
    }
}
