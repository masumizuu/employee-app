package com.gabriel.empms.controller;


import com.gabriel.empms.controller.storage.StorageService;
import com.gabriel.empms.model.Employee;
import com.gabriel.empms.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    private final StorageService storageService;

    @Autowired
    public EmployeeController(StorageService storageService) {
        this.storageService = storageService;
    }

    // ✅ Get All Employees
    @GetMapping
    public ResponseEntity<?> listEmployees() {
        try {
            Employee[] employees = employeeService.getEmployees();
            return ResponseEntity.ok().body(employees);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // ✅ Get Employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable final Integer id) {
        try {
            Employee employee = employeeService.getEmployee(id);
            return ResponseEntity.ok(employee);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // ✅ Add New Employee
    @PutMapping
    public ResponseEntity<?> add(@RequestBody Employee employee) {
        try {
            Employee newEmployee = employeeService.create(employee);
            return ResponseEntity.ok(newEmployee);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // ✅ Update Employee Details
    @PostMapping
    public ResponseEntity<?> update(@RequestBody Employee employee) {
        try {
            Employee updatedEmployee = employeeService.update(employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // ✅ Delete Employee
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable final Integer id) {
        try {
            employeeService.delete(id);
            return ResponseEntity.ok(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // ✅ Upload Profile Picture
    @PostMapping("/{id}/upload-profile")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                logger.error("File is empty for employee ID: " + id);
                return ResponseEntity.badRequest().body("File is empty!");
            }

            // 🔹 Ensure filename is prefixed with employee ID
            String filename = "employee_" + id + "_" + file.getOriginalFilename();

            logger.info("Saving file: " + filename);

            // 🔹 Store the file inside `upload-dir`
            storageService.store(file);

            // 🔹 Construct the file path for retrieval
            String fileUrl = "/upload-dir/" + filename;

            logger.info("File saved. Updating employee record in database.");

            // 🔹 Update employee profile picture in DB
            employeeService.updateProfilePicture(id, fileUrl);

            return ResponseEntity.ok("Profile picture uploaded successfully!");

        } catch (Exception e) {
            logger.error("Error uploading profile picture for employee ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile picture. Error: " + e.getMessage());
        }
    }


    @GetMapping("/profile/{filename:.+}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        System.out.println("🔍 DEBUG: Received request for " + filename); // 🔹 Force log output

        try {
            System.out.println("🔍 DEBUG: Calling storageService.loadAsResource()...");
            Resource file = storageService.loadAsResource(filename);

            Path filePath = Paths.get("upload-dir").resolve(filename).normalize();
            System.out.println("📂 Looking for file: " + filePath.toAbsolutePath());

            if (file.exists() || file.isReadable()) {
                String contentType = determineContentType(filename);
                System.out.println("✅ DEBUG: File found, returning with content type " + contentType);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(file);
            } else {
                System.out.println("❌ DEBUG: File not found at " + filePath.toAbsolutePath());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            System.out.println("❌ DEBUG: Exception occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ✅ Detect MIME type dynamically
    private String determineContentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".webp")) return "image/webp";
        return "application/octet-stream"; // Default unknown type
    }


    // ✅ Update Employee Status (Active, On Leave, Resigned)
    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateEmployeeStatus(@PathVariable int id, @RequestParam("status") String status) {
        try {
            employeeService.updateStatus(id, status);
            return ResponseEntity.ok("Employee status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update employee status.");
        }
    }
}
