 import java.io.*;
public class FileCopy { 
    public static void main(String[] args) {
        String inputFilePath = "C:\\Akhilareddy\\text1.txt"; // Source file
        String outputFilePath = "C:\\Akhilareddy\\text2.txt"; // Destination file

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))
        ) {
            String line;
            System.out.println("Contents of the input file:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print to console
                writer.write(line); // Write to output file
                writer.newLine(); // Maintain line breaks
            }
            System.out.println("\nFile copied successfully to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}