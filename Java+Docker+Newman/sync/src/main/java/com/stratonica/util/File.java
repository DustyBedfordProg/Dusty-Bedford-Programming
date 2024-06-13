package com.stratonica.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class File {

	public static List<String> readFile(String filePath) {
		List<String> lines = new ArrayList<String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));

			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			
			reader.close();
		} catch (Exception e) {
		}
		return lines;
	}

	public static String lastModified(String fileName) {
		Path file = Paths.get(fileName);

		BasicFileAttributes attr = null;
		try {
			attr = Files.readAttributes(file, BasicFileAttributes.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "" + attr.lastModifiedTime();
	}

	public static boolean exists(String fName1) {
		return (new java.io.File(fName1)).exists();
	}

	public static boolean create(String fName1) {
		try {
			return (new java.io.File(fName1)).createNewFile();
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(File.lastModified("C:/BaseCode/GenericWebDriver/webdriver/resources/text/f1/test.txt"));
	}

	public static void copy(String source, String dest) {
		try {

			Files.copy((new java.io.File(source)).toPath(), (new java.io.File(dest)).toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void writeToFile(String filename, List<String> strings) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			for (String line : strings) {
				writer.write(line);
				writer.newLine();
			}
			System.out.println("File writing completed successfully.");
		} catch (IOException e) {
			System.err.println("An error occurred while writing to the file: " + e.getMessage());
		}
	}

	public static <T> void writeObjToFile(String filename, List<T> strings) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			for (T line : strings) {
				writer.write(line.toString());
				writer.newLine();
			}
			System.out.println("File writing completed successfully.");
		} catch (IOException e) {
			System.err.println("An error occurred while writing to the file: " + e.getMessage());
		}
	}
}
