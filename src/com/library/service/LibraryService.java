package com.library.service;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.IssueRecord;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    private final FileManager fileManager;
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, IssueRecord> issues = new HashMap<>();

    // Default Admin Credentials
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    public LibraryService() {
        this.fileManager = new FileManager();
        loadData();
    }

    private void loadData() {
        List<Book> bookList = fileManager.loadBooks();
        for (Book b : bookList) {
            books.put(b.getId(), b);
        }

        List<Member> memberList = fileManager.loadMembers();
        for (Member m : memberList) {
            members.put(m.getId(), m);
        }

        List<IssueRecord> issueList = fileManager.loadIssues();
        for (IssueRecord r : issueList) {
            issues.put(r.getIssueId(), r);
        }
    }

    public void saveData() {
        fileManager.saveBooks(new ArrayList<>(books.values()));
        fileManager.saveMembers(new ArrayList<>(members.values()));
        fileManager.saveIssues(new ArrayList<>(issues.values()));
    }

    // ==========================================
    // Authentication
    // ==========================================
    public boolean authenticateAdmin(String username, String password) {
        return ADMIN_USER.equalsIgnoreCase(username) && ADMIN_PASS.equals(password);
    }

    public Member authenticateMember(String memberId, String password) {
        Member member = members.get(memberId);
        if (member != null && member.getPassword().equals(password)) {
            return member;
        }
        return null;
    }

    // ==========================================
    // Book Management
    // ==========================================
    public boolean addBook(Book book) {
        if (books.containsKey(book.getId())) {
            return false; // Book ID already exists
        }
        books.put(book.getId(), book);
        saveData();
        return true;
    }

    public boolean updateBook(String id, String title, String author, String category, int totalCopies) {
        Book book = books.get(id);
        if (book == null) return false;

        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setTotalCopies(totalCopies);
        saveData();
        return true;
    }

    public boolean deleteBook(String id) {
        Book book = books.get(id);
        if (book == null) return false;

        // Check if there are active issue records for this book
        boolean hasActiveIssues = issues.values().stream()
                .anyMatch(r -> r.getBookId().equals(id) && !r.isReturned());
        if (hasActiveIssues) {
            return false; // Can't delete, currently issued out
        }

        books.remove(id);
        saveData();
        return true;
    }

    public Collection<Book> getAllBooks() {
        return books.values();
    }

    public Book getBook(String id) {
        return books.get(id);
    }

    // Search methods
    public List<Book> searchBooks(String query, String type) {
        String lowerQuery = query.toLowerCase();
        return books.values().stream()
                .filter(b -> {
                    switch (type.toLowerCase()) {
                        case "id":
                            return b.getId().toLowerCase().contains(lowerQuery);
                        case "title":
                            return b.getTitle().toLowerCase().contains(lowerQuery);
                        case "author":
                            return b.getAuthor().toLowerCase().contains(lowerQuery);
                        case "category":
                            return b.getCategory().toLowerCase().contains(lowerQuery);
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // ==========================================
    // Member Management
    // ==========================================
    public boolean addMember(Member member) {
        if (members.containsKey(member.getId())) {
            return false;
        }
        members.put(member.getId(), member);
        saveData();
        return true;
    }

    public boolean updateMember(String id, String name, String email, String phone, String password) {
        Member member = members.get(id);
        if (member == null) return false;

        member.setName(name);
        member.setEmail(email);
        member.setPhone(phone);
        member.setPassword(password);
        saveData();
        return true;
    }

    public boolean deleteMember(String id) {
        Member member = members.get(id);
        if (member == null) return false;

        // Check if member has active issues or outstanding fines
        boolean hasActiveIssues = issues.values().stream()
                .anyMatch(r -> r.getMemberId().equals(id) && !r.isReturned());
        if (hasActiveIssues) {
            return false; // Cannot delete, member has active borrows
        }

        if (member.getFineBalance() > 0) {
            return false; // Cannot delete, member has unpaid fines
        }

        members.remove(id);
        saveData();
        return true;
    }

    public Collection<Member> getAllMembers() {
        return members.values();
    }

    public Member getMember(String id) {
        return members.get(id);
    }

    // ==========================================
    // Issue and Return Management
    // ==========================================
    public synchronized String issueBook(String bookId, String memberId, LocalDate issueDate) throws Exception {
        Book book = books.get(bookId);
        if (book == null) {
            throw new Exception("Book not found!");
        }
        Member member = members.get(memberId);
        if (member == null) {
            throw new Exception("Member not found!");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new Exception("Book is currently out of stock!");
        }

        // Limit checking: check if member already has this book issued and not returned
        boolean alreadyIssued = issues.values().stream()
                .anyMatch(r -> r.getBookId().equals(bookId) && r.getMemberId().equals(memberId) && !r.isReturned());
        if (alreadyIssued) {
            throw new Exception("This book is already issued to this member and not yet returned!");
        }

        // Limit checking: check if member has outstanding fines above 50.0
        if (member.getFineBalance() > 50.0) {
            throw new Exception("Member has outstanding fines exceeding $50.00. Clear fines first.");
        }

        // Issue book
        String issueId = "I" + String.format("%03d", issues.size() + 1);
        IssueRecord record = new IssueRecord(issueId, bookId, memberId, issueDate);
        issues.put(issueId, record);

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        saveData();
        
        return issueId;
    }

    public synchronized double returnBook(String issueId, LocalDate returnDate) throws Exception {
        IssueRecord record = issues.get(issueId);
        if (record == null) {
            throw new Exception("Issue record not found!");
        }
        if (record.isReturned()) {
            throw new Exception("This book has already been returned!");
        }

        Book book = books.get(record.getBookId());
        Member member = members.get(record.getMemberId());

        if (returnDate.isBefore(record.getIssueDate())) {
            throw new Exception("Return date cannot be before issue date!");
        }

        record.setReturnDate(returnDate);
        record.setReturned(true);

        // Update book availability
        if (book != null) {
            book.setAvailableCopies(Math.min(book.getTotalCopies(), book.getAvailableCopies() + 1));
        }

        // Calculate and apply fine to member balance
        double fine = record.calculateFine(returnDate);
        if (fine > 0 && member != null) {
            member.addFine(fine);
        }

        saveData();
        return fine;
    }

    public List<IssueRecord> getMemberActiveIssues(String memberId) {
        return issues.values().stream()
                .filter(r -> r.getMemberId().equals(memberId) && !r.isReturned())
                .collect(Collectors.toList());
    }

    public List<IssueRecord> getMemberAllIssues(String memberId) {
        return issues.values().stream()
                .filter(r -> r.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public Collection<IssueRecord> getAllIssues() {
        return issues.values();
    }

    public IssueRecord getIssueRecord(String issueId) {
        return issues.get(issueId);
    }

    // ==========================================
    // Fine Management
    // ==========================================
    public double payMemberFine(String memberId, double amount) throws Exception {
        Member member = members.get(memberId);
        if (member == null) {
            throw new Exception("Member not found!");
        }
        if (amount <= 0) {
            throw new Exception("Payment amount must be greater than zero.");
        }
        member.payFine(amount);
        saveData();
        return member.getFineBalance();
    }

    // ==========================================
    // Availability Lists
    // ==========================================
    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(b -> b.getAvailableCopies() > 0)
                .collect(Collectors.toList());
    }

    public List<Book> getOutOfStockBooks() {
        return books.values().stream()
                .filter(b -> b.getAvailableCopies() == 0)
                .collect(Collectors.toList());
    }

    public List<Book> getIssuedBooksList() {
        return books.values().stream()
                .filter(b -> b.getAvailableCopies() < b.getTotalCopies())
                .collect(Collectors.toList());
    }

    // ==========================================
    // Reports
    // ==========================================
    public Map<String, Object> getReportsData(LocalDate currentDate) {
        Map<String, Object> reports = new HashMap<>();
        
        int totalBooks = books.values().stream().mapToInt(Book::getTotalCopies).sum();
        int totalUniqueBooks = books.size();
        int totalMembers = members.size();
        
        long activeIssues = issues.values().stream().filter(r -> !r.isReturned()).count();
        long availableBooks = books.values().stream().mapToInt(Book::getAvailableCopies).sum();
        
        long overdueBooks = issues.values().stream()
                .filter(r -> !r.isReturned() && currentDate.isAfter(r.getDueDate()))
                .count();

        double totalPendingFines = members.values().stream().mapToDouble(Member::getFineBalance).sum();

        reports.put("totalBooks", totalBooks);
        reports.put("totalUniqueBooks", totalUniqueBooks);
        reports.put("totalMembers", totalMembers);
        reports.put("activeIssues", activeIssues);
        reports.put("availableBooks", availableBooks);
        reports.put("overdueBooks", overdueBooks);
        reports.put("totalPendingFines", totalPendingFines);

        return reports;
    }
}
