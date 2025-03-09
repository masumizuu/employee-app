package com.gabriel.empms.model;

import lombok.Data;
import java.time.LocalDate;


@Data
public class Employee {
    int id;
    String name;
    String profilePicture; // Path or URL for the employee's profile picture
    String department;
    String position;
    LocalDate hireDate; // Employed since
    LocalDate birthday;
    double salary;
    String status; // Active, On Leave, Resigned
    String[] achievements; // List of badges/achievements
}