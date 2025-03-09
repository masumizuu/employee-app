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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

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
    public ResponseEntity<?> uploadProfilePicture(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        try {
            String filename = "employee_" + id + "_" + file.getOriginalFilename();
            storageService.store(file); // Save file

            String fileUrl = "/api/employee/profile/" + filename;
            employeeService.updateProfilePicture(id, fileUrl);

            return ResponseEntity.created(URI.create(fileUrl)).body("Profile picture uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture.");
        }
    }

    // ✅ Retrieve Profile Picture
    @GetMapping("/profile/{filename:.+}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        try {
            Resource file = storageService.loadAsResource(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
