package com.hexaware.easypay.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hexaware.easypay.customexceptions.ResourceNotFoundException;
import com.hexaware.easypay.dto.PayrollDTO;
import com.hexaware.easypay.model.PayrollStatus;
import com.hexaware.easypay.service.IPayrollService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payrolls")
@PreAuthorize("hasRole('ADMIN')") // Only admin can access payroll functions
public class PayrollController {

	@Autowired
    private IPayrollService payrollService;

    @Autowired
    public PayrollController(IPayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping
    public ResponseEntity<PayrollDTO> createPayroll(@Valid @RequestBody PayrollDTO payrollDTO) 
            throws ResourceNotFoundException {
        return new ResponseEntity<>(payrollService.createPayroll(payrollDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollDTO> getPayrollById(@PathVariable Long id) 
            throws ResourceNotFoundException {
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
    }

    @GetMapping("/period")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(payrollService.getPayrollsByPeriod(startDate, endDate));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByStatus(@PathVariable PayrollStatus status) {
        return ResponseEntity.ok(payrollService.getPayrollsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayrollDTO> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollDTO payrollDTO) throws ResourceNotFoundException {
        return ResponseEntity.ok(payrollService.updatePayroll(id, payrollDTO));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<Map<String, Object>> processPayroll(@PathVariable Long id) {
        try {
            Map<String, Object> result = payrollService.processPayroll(id);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approvePayroll(@PathVariable Long id) {
        try {
            Map<String, Object> result = payrollService.approvePayroll(id);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/test")
    public String test() {
        return "Payroll Controller is working!";
    }
}