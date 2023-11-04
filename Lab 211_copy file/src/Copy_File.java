/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author dotha
 */
public class Copy_File {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String CONFIG_FILE = "config.properties";

    public static void main(String[] args) {
        FileCopyManager fileCopyManager = new FileCopyManager();
        Properties properties = fileCopyManager.readConfig(CONFIG_FILE);
        String copyFolder = properties.getProperty("COPY_FOLDER");
        String path = properties.getProperty("PATH");

        if (copyFolder.isEmpty() || path.isEmpty()) {
            System.err.println("Copy Folder and/or Path is not provided.");
            System.out.println("System shutdown!");
            return;
        }

        fileCopyManager.copyFiles(copyFolder, path);
        System.out.println("Copy is finished...");
    }
}

class FileCopyManager {

    public Properties readConfig(String configFile) {
        Properties prop = new Properties();

        try (FileReader reader = new FileReader(configFile)) {
            prop.load(reader);
        } catch (FileNotFoundException ex) {
            System.out.println("Config file not found. Creating a new one.");
            createConfig(configFile, prop);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (prop.getProperty("COPY_FOLDER") == null || prop.getProperty("PATH") == null) {
            System.out.println("Configuration is incomplete. Updating configuration.");
            createConfig(configFile, prop);
        }

        return prop;
    }

    public void createConfig(String configFile, Properties prop) {
        try (OutputStream output = new FileOutputStream(configFile)) {
            System.out.print("Copy Folder: ");
            Scanner localScanner = new Scanner(System.in); // Create a new scanner for user input
            String copyFolder = localScanner.nextLine();
            System.out.print("Data Type: ");
            String dataType = localScanner.nextLine();
            System.out.print("Path: ");
            String path = localScanner.nextLine();
            prop.setProperty("COPY_FOLDER", copyFolder);
            prop.setProperty("DATA_TYPE", dataType);
            prop.setProperty("PATH", path);
            prop.store(output, null);

            // Close the localScanner
            localScanner.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("File configuration cannot be created.");
            System.out.println("System shutdown");
            System.exit(1);
        }
    }

    public void copyFiles(String sourcePath, String destinationPath) {
        File sourceFolder = new File(sourcePath);
        File destinationFolder = new File(destinationPath);

        if (!checkInformationConfig(sourceFolder, destinationFolder)) {
            System.err.println("System shutdown");
            System.exit(1);
        }

        File[] listOfFiles = sourceFolder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    copyFile(file, new File(destinationFolder, file.getName()));
                    System.out.println("File name: " + file.getName());
                }
            }
        }
    }

    private void copyFile(File sourceFile, File destinationFile) {
        if (sourceFile.exists() && sourceFile.isFile() && destinationFile.getParentFile().exists()) {
            try (FileInputStream fis = new FileInputStream(sourceFile); FileOutputStream fos = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkInformationConfig(File sourceFolder, File destinationFolder) {
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            System.err.println("Source folder doesn't exist or is not a directory.");
            return false;
        }

        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            if (!destinationFolder.mkdirs()) {
                System.err.println("Destination folder cannot be created.");
                return false;
            }
        }

        return true;
    }
}