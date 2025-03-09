package com.gabriel.empms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "employee_data")
public class EmployeeData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String name;
    String profilePicture;
    String department;
    String position;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate hireDate; // Employed since

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;

    private double salary;
    String status; // Active, On Leave, Resigned

    @ElementCollection
    List<String> achievements; // Stores employee achievements as a list of strings

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date lastUpdated;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date created;
}