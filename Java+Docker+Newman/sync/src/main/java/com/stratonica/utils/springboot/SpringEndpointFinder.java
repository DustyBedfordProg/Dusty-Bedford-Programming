package com.stratonica.utils.springboot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpringEndpointFinder {

    private static final String PROJECT_DIRECTORY = "C:\\stcode";

    public static void main(String[] args) {
        List<String> endpoints = new ArrayList<String>();
        findEndpoints(new File(PROJECT_DIRECTORY), endpoints);
        endpoints.forEach(System.out::println);
    }

    private static void findEndpoints(File directory, List<String> endpoints) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                findEndpoints(file, endpoints);
            }
        } else if (directory.getName().endsWith(".java")) {
            extractEndpointsFromFile(directory, endpoints);
        }
    }

    private static void extractEndpointsFromFile(File file, List<String> endpoints) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("@RequestMapping") || line.contains("@GetMapping") ||
                    line.contains("@PostMapping") || line.contains("@PutMapping") ||
                    line.contains("@DeleteMapping")) {
                    String endpoint = extractEndpoint(line);
                    if (endpoint != null) {
                        endpoints.add(endpoint); //+ " in " + file.getPath());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractEndpoint(String annotationLine) {
        int start = annotationLine.indexOf('(');
        int end = annotationLine.indexOf(')');
        if (start != -1 && end != -1) {
            return annotationLine.substring(start + 1, end).replaceAll("\"", "").trim();
        }
        return null;
    }
}
