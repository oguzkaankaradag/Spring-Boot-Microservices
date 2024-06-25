package com.example.employee_service.controller;


import com.example.employee_service.dto.APIResponseDto;
import com.example.employee_service.dto.DepartmentDto;
import com.example.employee_service.dto.EmployeeDto;
import com.example.employee_service.service.EmployeeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employees")
@AllArgsConstructor
public class EmployeeController {

    private EmployeeService employeeService;

    // Build Save Employee REST API
    @PostMapping
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto employeeDto){
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    // Build Get Employee REST API
    @CircuitBreaker(name = "EMPLOYEE-SERVICE", fallbackMethod = "getDefaultEmployeeById")
    @GetMapping("{id}")
    public ResponseEntity<APIResponseDto> getEmployee(@PathVariable("id") Long employeeId){
        APIResponseDto apiResponseDto = employeeService.getEmployeeById(employeeId);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.OK);
    }

    // Fallback method for getEmployee
    public ResponseEntity<APIResponseDto> getDefaultEmployeeById(Long employeeId, Throwable throwable) {
        // Log the exception (optional)
        throwable.printStackTrace();

        // Create a default EmployeeDto
        EmployeeDto defaultEmployee = new EmployeeDto();
        defaultEmployee.setId(employeeId);
        defaultEmployee.setFirstName("Default");
        defaultEmployee.setLastName("Employee");
        defaultEmployee.setEmail("default@example.com");
        defaultEmployee.setDepartmentCode("DEFAULT_DEPT");

        // Create a default DepartmentDto
        DepartmentDto defaultDepartment = new DepartmentDto();
        defaultDepartment.setId(0L);
        defaultDepartment.setDepartmentName("Default Department");
        defaultDepartment.setDepartmentDescription("This is a default department");
        defaultDepartment.setDepartmentCode("DEFAULT_DEPT");

        // Create an APIResponseDto with the default data
        APIResponseDto defaultResponse = new APIResponseDto();
        defaultResponse.setEmployee(defaultEmployee);
        defaultResponse.setDepartment(defaultDepartment);

        // Return a response entity with default data and appropriate HTTP status
        return new ResponseEntity<>(defaultResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }



}
