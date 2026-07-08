package com.library.model;

public class Member {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private double fineBalance;

    public Member(String id, String name, String email, String phone, String password, double fineBalance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.fineBalance = fineBalance;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getFineBalance() {
        return fineBalance;
    }

    public void setFineBalance(double fineBalance) {
        this.fineBalance = fineBalance;
    }

    public void addFine(double amount) {
        this.fineBalance += amount;
    }

    public void payFine(double amount) {
        this.fineBalance = Math.max(0.0, this.fineBalance - amount);
    }

    // CSV format helper
    public String toCSV() {
        return escapeCSV(id) + "," +
               escapeCSV(name) + "," +
               escapeCSV(email) + "," +
               escapeCSV(phone) + "," +
               escapeCSV(password) + "," +
               fineBalance;
    }

    public static Member fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6) return null;
        
        String id = unescapeCSV(parts[0]);
        String name = unescapeCSV(parts[1]);
        String email = unescapeCSV(parts[2]);
        String phone = unescapeCSV(parts[3]);
        String password = unescapeCSV(parts[4]);
        double fineBalance = Double.parseDouble(parts[5]);
        
        return new Member(id, name, email, phone, password, fineBalance);
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
        return String.format("ID: %-6s | Name: %-20s | Email: %-25s | Phone: %-12s | Fines: $%.2f",
                id, name, email, phone, fineBalance);
    }
}
