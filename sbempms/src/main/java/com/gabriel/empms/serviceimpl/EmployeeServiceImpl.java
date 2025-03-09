package com.gabriel.empms.serviceimpl;

import com.gabriel.empms.entity.EmployeeData;
import com.gabriel.empms.model.Employee;
import com.gabriel.empms.repository.EmployeeDataRepository;
import com.gabriel.empms.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    EmployeeDataRepository employeeDataRepository;

    @Override
    public Employee[] getEmployees() {
        List<EmployeeData> employeesData = (List<EmployeeData>) employeeDataRepository.findAll();
        List<Employee> employees = new ArrayList<>();

        for (EmployeeData employeeData : employeesData) {
            employees.add(convertToModel(employeeData));
        }

        return employees.toArray(new Employee[0]);
    }

    @Override
    public Employee create(Employee employee) {
        logger.info("Adding employee: " + employee.toString());
        EmployeeData employeeData = new EmployeeData();

        // Populate all fields from Employee model
        employeeData.setName(employee.getName());
        employeeData.setProfilePicture(employee.getProfilePicture());
        employeeData.setDepartment(employee.getDepartment());
        employeeData.setPosition(employee.getPosition());
        employeeData.setHireDate(employee.getHireDate());
        employeeData.setBirthday(employee.getBirthday());
        employeeData.setSalary(employee.getSalary());
        employeeData.setStatus(employee.getStatus());
        employeeData.setAchievements(Arrays.asList(employee.getAchievements()));

        employeeData = employeeDataRepository.save(employeeData);
        logger.info("Employee added: " + employeeData.toString());

        return convertToModel(employeeData);
    }

    @Override
    public Employee update(Employee employee) {
        Optional<EmployeeData> optional = employeeDataRepository.findById(employee.getId());

        if (optional.isPresent()) {
            EmployeeData employeeData = optional.get();

            // Update all fields
            employeeData.setName(employee.getName());
            employeeData.setProfilePicture(employee.getProfilePicture());
            employeeData.setDepartment(employee.getDepartment());
            employeeData.setPosition(employee.getPosition());
            employeeData.setHireDate(employee.getHireDate());
            employeeData.setBirthday(employee.getBirthday());
            employeeData.setSalary(employee.getSalary());
            employeeData.setStatus(employee.getStatus());
            employeeData.setAchievements(Arrays.asList(employee.getAchievements()));

            employeeData = employeeDataRepository.save(employeeData);
            return convertToModel(employeeData);
        } else {
            logger.warn("Failed to update: Employee not found.");
            return null;
        }
    }

    @Override
    public Employee getEmployee(Integer id) {
        logger.info("Fetching employee with ID: " + id);
        Optional<EmployeeData> optional = employeeDataRepository.findById(id);

        return optional.map(this::convertToModel).orElse(null);
    }

    @Override
    public void delete(Integer id) {
        logger.info("Deleting employee with ID: " + id);
        Optional<EmployeeData> optional = employeeDataRepository.findById(id);

        optional.ifPresentOrElse(employeeDataRepository::delete,
                () -> logger.warn("Failed to delete: Employee not found."));
    }

    // ✅ New Method: Update Employee Profile Picture
    @Override
    public void updateProfilePicture(int id, String filePath) {
        Optional<EmployeeData> optional = employeeDataRepository.findById(id);

        if (optional.isPresent()) {
            EmployeeData employeeData = optional.get();
            employeeData.setProfilePicture(filePath);
            employeeDataRepository.save(employeeData);
            logger.info("Updated profile picture for employee ID: " + id);
        } else {
            logger.warn("Failed to update profile picture: Employee not found.");
        }
    }

    // ✅ New Method: Update Employee Status
    @Override
    public void updateStatus(int id, String status) {
        Optional<EmployeeData> optional = employeeDataRepository.findById(id);

        if (optional.isPresent()) {
            EmployeeData employeeData = optional.get();
            employeeData.setStatus(status);
            employeeDataRepository.save(employeeData);
            logger.info("Updated status for employee ID: " + id + " to " + status);
        } else {
            logger.warn("Failed to update status: Employee not found.");
        }
    }

    // 🔹 Utility method to convert Entity to Model
    private Employee convertToModel(EmployeeData employeeData) {
        Employee employee = new Employee();
        employee.setId(employeeData.getId());
        employee.setName(employeeData.getName());
        employee.setProfilePicture(employeeData.getProfilePicture());
        employee.setDepartment(employeeData.getDepartment());
        employee.setPosition(employeeData.getPosition());
        employee.setHireDate(employeeData.getHireDate());
        employee.setBirthday(employeeData.getBirthday());
        employee.setSalary(employeeData.getSalary());
        employee.setStatus(employeeData.getStatus());
        employee.setAchievements(employeeData.getAchievements().toArray(new String[0]));
        return employee;
    }
}
