package com.gabriel.empms.service;

import com.gabriel.empms.model.Employee;

public interface EmployeeService {
    Employee[] getEmployees() throws Exception;

    Employee getEmployee(Integer id) throws Exception;

    Employee create(Employee product) throws Exception;

    Employee update(Employee product) throws Exception;

    void delete(Integer id) throws Exception;

    // ✅ New Method: Update Employee Profile Picture
    void updateProfilePicture(int id, String filePath) throws Exception;

    // ✅ New Method: Update Employee Status (Active, On Leave, Resigned)
    void updateStatus(int id, String status) throws Exception;
}
