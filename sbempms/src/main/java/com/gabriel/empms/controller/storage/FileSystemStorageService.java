package com.gabriel.empms.controller.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);


    StorageProperties properties;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.properties = properties;

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            // 🔹 Define the absolute path to the `upload-dir`
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "upload-dir");

            // 🔹 Ensure the directory exists
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);  // ✅ This ensures `upload-dir/` is created
                logger.info("Upload directory created: " + uploadDir.toAbsolutePath());
            }

            // 🔹 Define destination file path
            Path destinationFile = uploadDir.resolve(file.getOriginalFilename()).normalize().toAbsolutePath();

            logger.info("Saving file to: " + destinationFile);

            // 🔹 Security check to prevent directory traversal attack
            if (!destinationFile.getParent().equals(uploadDir.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            // 🔹 Save the file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.info("File saved successfully!");

        } catch (IOException e) {
            logger.info("Failed to store file.", e);
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        System.out.println("🔍 DEBUG: Trying to load file: " + filename);

        try {
            Path file = Paths.get("upload-dir").resolve(filename).normalize().toAbsolutePath(); // ✅ Absolute path
            System.out.println("📂 DEBUG: Resolved file path: " + file.toAbsolutePath());

            if (!Files.exists(file)) {
                System.out.println("❌ DEBUG: File NOT FOUND at " + file.toAbsolutePath());
                throw new StorageFileNotFoundException("File not found: " + file.toAbsolutePath());
            }

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("❌ DEBUG: File exists but is NOT readable!");
                throw new StorageFileNotFoundException("File exists but is not readable: " + file.toAbsolutePath());
            }

            System.out.println("✅ DEBUG: File loaded successfully.");
            return resource;
        } catch (MalformedURLException e) {
            System.out.println("❌ DEBUG: MalformedURLException: " + e.getMessage());
            throw new StorageFileNotFoundException("Could not load file: " + filename, e);
        }
    }


    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}