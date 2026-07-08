package com.library.model;

public class Book {
    private String id;
    private String title;
    private String author;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public Book(String id, String title, String author, String category, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        // Adjust available copies based on change in total copies
        int diff = totalCopies - this.totalCopies;
        this.totalCopies = totalCopies;
        this.availableCopies = Math.max(0, this.availableCopies + diff);
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // CSV format helper
    public String toCSV() {
        // Replace commas with semicolons or handle escape to avoid CSV parsing errors
        return escapeCSV(id) + "," +
               escapeCSV(title) + "," +
               escapeCSV(author) + "," +
               escapeCSV(category) + "," +
               totalCopies + "," +
               availableCopies;
    }

    public static Book fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6) return null;
        
        String id = unescapeCSV(parts[0]);
        String title = unescapeCSV(parts[1]);
        String author = unescapeCSV(parts[2]);
        String category = unescapeCSV(parts[3]);
        int totalCopies = Integer.parseInt(parts[4]);
        int availableCopies = Integer.parseInt(parts[5]);
        
        return new Book(id, title, author, category, totalCopies, availableCopies);
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace(",", "\\,");
    }

    private static String unescapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\\,", ",");
    }

    @Override
    public String toString() {
        return String.format("ID: %-6s | Title: %-25s | Author: %-20s | Category: %-15s | Available: %d/%d",
                id, title, author, category, availableCopies, totalCopies);
    }
}
