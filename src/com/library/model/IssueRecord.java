package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class IssueRecord {
    private String issueId;
    private String bookId;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;

    private static final double FINE_PER_DAY = 5.0; // 5 currency units per day

    public IssueRecord(String issueId, String bookId, String memberId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, boolean isReturned) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }

    public IssueRecord(String issueId, String bookId, String memberId, LocalDate issueDate) {
        this(issueId, bookId, memberId, issueDate, issueDate.plusDays(14), null, false);
    }

    // Getters and Setters
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    /**
     * Calculates the fine based on either the return date or the current date if not yet returned.
     */
    public double calculateFine(LocalDate dateForCalculation) {
        LocalDate endPoint = isReturned ? returnDate : dateForCalculation;
        if (endPoint == null || endPoint.isBefore(dueDate) || endPoint.isEqual(dueDate)) {
            return 0.0;
        }
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, endPoint);
        return daysOverdue * FINE_PER_DAY;
    }

    public long getDaysOverdue(LocalDate dateForCalculation) {
        LocalDate endPoint = isReturned ? returnDate : dateForCalculation;
        if (endPoint == null || endPoint.isBefore(dueDate) || endPoint.isEqual(dueDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, endPoint);
    }

    // CSV format helpers
    public String toCSV() {
        return issueId + "," +
               bookId + "," +
               memberId + "," +
               issueDate + "," +
               dueDate + "," +
               (returnDate != null ? returnDate.toString() : "null") + "," +
               isReturned;
    }

    public static IssueRecord fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 7) return null;
        
        String issueId = parts[0];
        String bookId = parts[1];
        String memberId = parts[2];
        LocalDate issueDate = LocalDate.parse(parts[3]);
        LocalDate dueDate = LocalDate.parse(parts[4]);
        LocalDate returnDate = parts[5].equals("null") ? null : LocalDate.parse(parts[5]);
        boolean isReturned = Boolean.parseBoolean(parts[6]);
        
        return new IssueRecord(issueId, bookId, memberId, issueDate, dueDate, returnDate, isReturned);
    }

    @Override
    public String toString() {
        return String.format("IssueID: %-6s | BookID: %-6s | MemberID: %-6s | Issued: %s | Due: %s | Returned: %-10s | Status: %s",
                issueId, bookId, memberId, issueDate, dueDate, 
                (returnDate != null ? returnDate.toString() : "Pending"),
                (isReturned ? "Returned" : "Active"));
    }
}
