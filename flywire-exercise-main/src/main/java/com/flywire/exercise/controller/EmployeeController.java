package com.flywire.exercise.controller;

import com.flywire.exercise.exception.ValidationException;
import com.flywire.exercise.model.Employee;
import com.flywire.exercise.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/active")
    public List<Employee> getAllActiveEmployees() {
        return employeeService.getAllActiveEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/date-range")
    public List<Employee> getEmployeesByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        return employeeService.getEmployeesByDateRange(startDate, endDate);
    }

    @PostMapping
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        try {
            employeeService.addEmployee(employee);
            return new ResponseEntity<>("Employee added successfully", HttpStatus.CREATED);
        } catch (ValidationException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/deactivate/{id}")
    public void deactivateEmployee(@PathVariable int id) {
        employeeService.deactivateEmployee(id);
    }
}
