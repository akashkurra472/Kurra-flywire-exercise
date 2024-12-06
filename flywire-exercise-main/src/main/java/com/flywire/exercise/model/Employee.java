package com.flywire.exercise.model;

import lombok.Data;

import java.util.List;

@Data
public class Employee {
    private int id;
    private String name;
    private String position;
    private boolean active;
    private List<Integer> directReports;
    private String hireDate;
    private List<String> directHireNames;
}
