package com.flywire.exercise.service;

import com.flywire.exercise.exception.ValidationException;
import com.flywire.exercise.model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private List<Employee> employees;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @PostConstruct
    public void init() throws IOException {
        employees = mapper.readValue(Paths.get("src/main/resources/json/data.json").toFile(), new TypeReference<List<Employee>>() {});
    }

    public List<Employee> getAllActiveEmployees() {
        return employees.stream()
                .filter(Employee::isActive)
                .sorted((e1, e2) -> e1.getName().split(" ")[1].compareTo(e2.getName().split(" ")[1]))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(int id) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
        if (employee != null) {
            List<String> directHireNames = employee.getDirectReports().stream()
                    .map(this::getEmployeeById)
                    .filter(Objects::nonNull)
                    .map(Employee::getName)
                    .collect(Collectors.toList());
            employee.setDirectHireNames(directHireNames);
        }
        return employee;
    }

    public List<Employee> getEmployeesByDateRange(String startDate, String endDate) {
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            return employees.stream()
                    .filter(e -> {
                        try {
                            Date hireDate = dateFormat.parse(e.getHireDate());
                            return hireDate.after(start) && hireDate.before(end);
                        } catch (ParseException ex) {
                            return false;
                        }
                    })
                    .sorted((e1, e2) -> {
                        try {
                            return dateFormat.parse(e2.getHireDate()).compareTo(dateFormat.parse(e1.getHireDate()));
                        } catch (ParseException ex) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            return null;
        }
    }

    public void addEmployee(Employee employee) {
        validateEmployee(employee);
        employees.add(employee);
        saveEmployeesToFile();
    }

    public void deactivateEmployee(int id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            throw new ValidationException("Employee with ID " + id + " does not exist");
        }
        if (!employee.isActive()) {
            throw new ValidationException("Employee with ID " + id + " is already deactivated");
        }
        employee.setActive(false);
        saveEmployeesToFile();
    }

    private void saveEmployeesToFile() {
        try {
            mapper.writeValue(Paths.get("src/main/resources/json/data.json").toFile(), employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().isEmpty()) {
            throw new ValidationException("Employee name is required");
        }
        if (employee.getPosition() == null || employee.getPosition().isEmpty()) {
            throw new ValidationException("Employee position is required");
        }
        if (employee.getHireDate() == null || employee.getHireDate().isEmpty()) {
            throw new ValidationException("Employee hire date is required");
        }
        try {
            dateFormat.parse(employee.getHireDate());
        } catch (ParseException e) {
            throw new ValidationException("Invalid hire date format");
        }
        if (employee.getDirectReports() == null) {
            throw new ValidationException("Direct reports list cannot be null");
        }
    }
}