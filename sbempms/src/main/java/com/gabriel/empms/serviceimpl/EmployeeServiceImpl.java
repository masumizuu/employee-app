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
        List<EmployeeData> employeesData = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();
        employeeDataRepository.findAll().forEach(employeesData::add);
        Iterator<EmployeeData> it = employeesData.iterator();

        while(it.hasNext()) {
            Employee employee = new Employee();
            EmployeeData employeeData = it.next();
            employee.setId(employeeData.getId());
            employee.setName(employeeData.getName());
            employees.add(employee);
        }

        Employee[] array = new Employee[employees.size()];
        for  (int i=0; i<employees.size(); i++){
            array[i] = employees.get(i);
        }
//        Employee[] array = (Employee[])employees.toArray();
        return array;
    }

    @Override
    public Employee create(Employee employee) {
        logger.info("add: Input"+ employee.toString());
        EmployeeData employeeData = new EmployeeData();
        employeeData.setName(employee.getName());

        employeeData = employeeDataRepository.save(employeeData);
        logger.info("add: Input"+ employeeData.toString());

        Employee newEmployee = new Employee();
        newEmployee.setId(employeeData.getId());
        newEmployee.setName(employeeData.getName());
        return newEmployee;
    }

    @Override
    public Employee update(Employee employee) {
        EmployeeData employeeData = new EmployeeData();
        employeeData.setId(employee.getId());
        employeeData.setName(employee.getName());

        employeeData = employeeDataRepository.save(employeeData);

        Employee newEmployee = new Employee();
        newEmployee.setId(employeeData.getId());
        newEmployee.setName(employeeData.getName());
        return newEmployee;
    }

    @Override
    public Employee getEmployee(Integer id) {
        logger.info("Input id >> "+  Integer.toString(id) );
        Optional<EmployeeData> optional = employeeDataRepository.findById(id);
        if(optional.isPresent()) {
            logger.info("Is present >> ");
            Employee employee = new Employee();
            EmployeeData employeeDatum = optional.get();
            employee.setId(employeeDatum.getId());
            employee.setName(employeeDatum.getName());
            return employee;
        }
        logger.info("Failed  >> unable to locate employee" );
        return null;
    }

    @Override
    public void delete(Integer id) {
        logger.info("Input >> " + Integer.toString(id));
         Optional<EmployeeData> optional = employeeDataRepository.findById(id);
         if( optional.isPresent()) {
             EmployeeData employeeDatum = optional.get();
             employeeDataRepository.delete(employeeDatum);
             logger.info("Success >> " + employeeDatum.toString());
         }
         else {
             logger.info("Failed  >> unable to locate employee id: " +  Integer.toString(id));
         }
    }
}
