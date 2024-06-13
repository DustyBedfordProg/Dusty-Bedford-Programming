package com.stratonica.util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public class Touch {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Touch <directory-path>");
            return;
        }

        File root = new File(args[0]);
        if (!root.exists() || !root.isDirectory()) {
            System.out.println("The provided path is not a valid directory.");
            return;
        }

        try {
            touchDirectory(root);
            System.out.println("Updated modification time for all files and directories in " + root.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("An error occurred while updating modification times: " + e.getMessage());
        }
    }

    public static void touchDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                touchDirectory(file);
            }
            touchFile(file);
        }
    }

    public static void touchFile(File file) throws IOException {
        Instant now = Instant.now();
        FileTime fileTime = FileTime.from(now);
        Files.setLastModifiedTime(file.toPath(), fileTime);
    }
}
