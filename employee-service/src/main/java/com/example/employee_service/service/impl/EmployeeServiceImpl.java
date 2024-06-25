package com.example.employee_service.service.impl;

import com.example.employee_service.dto.APIResponseDto;
import com.example.employee_service.dto.DepartmentDto;
import com.example.employee_service.dto.EmployeeDto;
import com.example.employee_service.entity.Employee;
import com.example.employee_service.mapper.EmployeeMapper;
import com.example.employee_service.repository.EmployeeRepository;
import com.example.employee_service.service.EmployeeService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeRepository employeeRepository;

    private WebClient webClient;


    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        Employee employee = EmployeeMapper.toEntity(employeeDto);
        Employee saveDEmployee = employeeRepository.save(employee);
        EmployeeDto savedEmployeeDto = EmployeeMapper.toDto(saveDEmployee);
        return savedEmployeeDto;
    }

    //    @CircuitBreaker(name = "EMPLOYEE-SERVICE", fallbackMethod = "getDefaultDepartment")
    @Retry(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    @Override
    public APIResponseDto getEmployeeById(Long employeeId) {

        LOGGER.info("inside the getEmployeeById method");

        Employee employee = employeeRepository.findById(employeeId).get();

        DepartmentDto departmentDto = webClient.get().uri("http://localhost:8080/api/departments/" + employee.getDepartmentCode()).retrieve().bodyToMono(DepartmentDto.class).block();


        EmployeeDto employeeDto = EmployeeMapper.toDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

        return apiResponseDto;
    }

    // Fallback method for getEmployee
    public ResponseEntity<APIResponseDto> getDefaultDepartment(Long employeeId, Throwable throwable) {
        // Log the exception (optional)
//        throwable.printStackTrace();
        LOGGER.info("inside the getDefaultDepartment method");

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
