/*
 * Creation : Jun 5, 2023
 */
package com.example.tax.TaxApp.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.tax.TaxApp.bean.Employee;
import com.example.tax.TaxApp.bean.EmployeeBean;
import com.example.tax.TaxApp.exception.BadRequest;
import com.example.tax.TaxApp.repo.EmpRepo;

@RestController
public class Controller {

    @Autowired
    EmpRepo repo;

    @PostMapping("/saveEmployeeDetails")
    public ResponseEntity<?> getFullname(@RequestBody @Validated EmployeeBean employee) throws BadRequest {
        Optional<EmployeeBean> bean = Optional.ofNullable(employee);
        if (bean.get().getFirstName().isEmpty()) {
            throw new BadRequest("First name should not be empty");

        }
        if (bean.get().getEmail().isEmpty()) {
            throw new BadRequest("Last name should not be Empty");
        }
        if (bean.get().getEmpId() <= 0) {
            throw new BadRequest("Enter valid EmpID");
        }
        if (bean.get().getPhoneNo().isEmpty()) {
            throw new BadRequest("Phone Number Should not be empty");
        }
        if (bean.get().getSalary() <= 1) {
            throw new BadRequest("Please enter valid salry");
        }

        Employee emp = new Employee();
        emp.setEmail(employee.getEmail());
        emp.setEmpId(employee.getEmpId());
        emp.setFirstName(employee.getFirstName());
        emp.setJoiningDate(employee.getJoiningDate());
        emp.setLastName(employee.getLastName());
        emp.setPhoneNo(employee.getPhoneNo().stream().collect(Collectors.joining(",")));
        emp.setSalary(employee.getSalary());
        System.out.println(emp.getJoiningDate());
        repo.save(emp);
        return new ResponseEntity<EmployeeBean>(HttpStatus.OK);
    }

    @PostMapping("/getTax")
    public EmployeeBean calculateTax(@RequestBody EmployeeBean employee) {

        Date input = new Date();
        input = employee.getJoiningDate();
        LocalDate date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        System.out.println(date);
        LocalDate endDate = LocalDate.of(2024, Month.APRIL, 30);
        Period datePeriod = Period.between(date, endDate);

        long value = ChronoUnit.MONTHS.between(date, endDate);
        Double yearSalary = employee.getSalary() * value;
        if (date.getDayOfMonth() > 15) {
            double perDaySalary = employee.getSalary() / 30;
            double salaryThisMonth = (perDaySalary * 15);
            yearSalary = yearSalary - salaryThisMonth;
        }
        double tax = 0;
        double cess = 0;
        if (yearSalary > 250000 && yearSalary <= 500000) {
            tax = (yearSalary - 250000) * 0.05;
        } else if (yearSalary > 500000 && yearSalary <= 1000000)
            tax = (yearSalary - 500000) * 0.2 + 250000 * 0.05;
        else if (yearSalary > 1000000) {
            tax = (yearSalary - 1000000) * 0.3 + 500000 * 0.2 + 250000 * 0.05;
        }

        if (yearSalary > 2800000) {
            cess = 2800000 * 0.02;
        }

        employee.setTotalTax(tax + cess);
        return employee;
    }

}
