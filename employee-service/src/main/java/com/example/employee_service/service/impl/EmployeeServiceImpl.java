package com.example.employee_service.service.impl;

import com.example.employee_service.dto.APIResponseDto;
import com.example.employee_service.dto.DepartmentDto;
import com.example.employee_service.dto.EmployeeDto;
import com.example.employee_service.dto.OrganizationDto;
import com.example.employee_service.entity.Employee;
import com.example.employee_service.mapper.EmployeeMapper;
import com.example.employee_service.repository.EmployeeRepository;
import com.example.employee_service.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(savedEmployee);
    }

    @Override
    public APIResponseDto getEmployeeById(Long employeeId) {
        LOGGER.info("inside the getEmployeeById method");

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        DepartmentDto departmentDto = webClient.get()
                .uri("http://localhost:8080/api/departments/" + employee.getDepartmentCode())
                .retrieve()
                .bodyToMono(DepartmentDto.class)
                .block();

        OrganizationDto organizationDto = webClient.get()
                .uri("http://localhost:8083/api/organizations/code/" + employee.getOrganizationCode())
                .retrieve()
                .bodyToMono(OrganizationDto.class)
                .block();

        EmployeeDto employeeDto = EmployeeMapper.toDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);
        apiResponseDto.setOrganization(organizationDto); // Set organization details

        return apiResponseDto;
    }

    // Fallback method for getEmployeeById
    public APIResponseDto getDefaultDepartment(Long employeeId, Throwable throwable) {
        LOGGER.info("inside the getDefaultDepartment method");

        EmployeeDto defaultEmployee = new EmployeeDto();
        defaultEmployee.setId(employeeId);
        defaultEmployee.setFirstName("Default");
        defaultEmployee.setLastName("Employee");
        defaultEmployee.setEmail("default@example.com");
        defaultEmployee.setDepartmentCode("DEFAULT_DEPT");
        defaultEmployee.setOrganizationCode("DEFAULT_ORG");

        DepartmentDto defaultDepartment = new DepartmentDto();
        defaultDepartment.setId(0L);
        defaultDepartment.setDepartmentName("Default Department");
        defaultDepartment.setDepartmentDescription("This is a default department");
        defaultDepartment.setDepartmentCode("DEFAULT_DEPT");

        OrganizationDto defaultOrganization = new OrganizationDto();
        defaultOrganization.setId(0L);
        defaultOrganization.setOrganizationName("Default Organization");
        defaultOrganization.setOrganizationDescription("This is a default organization");
        defaultOrganization.setOrganizationCode("DEFAULT_ORG");

        APIResponseDto defaultResponse = new APIResponseDto();
        defaultResponse.setEmployee(defaultEmployee);
        defaultResponse.setDepartment(defaultDepartment);
        defaultResponse.setOrganization(defaultOrganization);

        return defaultResponse;
    }
}
