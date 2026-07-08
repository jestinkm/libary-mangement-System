package com.library.service;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.IssueRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String BOOKS_FILE = DATA_DIR + "/books.txt";
    private static final String MEMBERS_FILE = DATA_DIR + "/members.txt";
    private static final String ISSUES_FILE = DATA_DIR + "/issues.txt";

    public FileManager() {
        try {
            // Ensure data directory exists
            Path dirPath = Paths.get(DATA_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
        }
    }

    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        if (!file.exists()) return books;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    Book book = Book.fromCSV(line);
                    if (book != null) {
                        books.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing book line: " + line + ". Skipping. " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books file: " + e.getMessage());
        }
        return books;
    }

    public void saveBooks(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                pw.println(book.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error writing to books file: " + e.getMessage());
        }
    }

    public List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        File file = new File(MEMBERS_FILE);
        if (!file.exists()) return members;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    Member member = Member.fromCSV(line);
                    if (member != null) {
                        members.add(member);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing member line: " + line + ". Skipping. " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading members file: " + e.getMessage());
        }
        return members;
    }

    public void saveMembers(List<Member> members) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member member : members) {
                pw.println(member.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error writing to members file: " + e.getMessage());
        }
    }

    public List<IssueRecord> loadIssues() {
        List<IssueRecord> records = new ArrayList<>();
        File file = new File(ISSUES_FILE);
        if (!file.exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    IssueRecord record = IssueRecord.fromCSV(line);
                    if (record != null) {
                        records.add(record);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing issue record line: " + line + ". Skipping. " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading issue records file: " + e.getMessage());
        }
        return records;
    }

    public void saveIssues(List<IssueRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ISSUES_FILE))) {
            for (IssueRecord record : records) {
                pw.println(record.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error writing to issue records file: " + e.getMessage());
        }
    }
}
