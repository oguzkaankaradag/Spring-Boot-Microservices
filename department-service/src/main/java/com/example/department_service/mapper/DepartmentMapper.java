package com.example.department_service.mapper;

import com.example.department_service.dto.DepartmentDto;
import com.example.department_service.entity.Department;

public class DepartmentMapper {

    // Convert Department entity to DepartmentDto
    public static DepartmentDto toDto(Department department) {
        if (department == null) {
            return null;
        }
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(department.getId());
        departmentDto.setDepartmentName(department.getDepartmentName());
        departmentDto.setDepartmentDescription(department.getDepartmentDescription());
        departmentDto.setDepartmentCode(department.getDepartmentCode());
        return departmentDto;
    }

    // Convert DepartmentDto to Department entity
    public static Department toEntity(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            return null;
        }
        Department department = new Department();
        department.setId(departmentDto.getId());
        department.setDepartmentName(departmentDto.getDepartmentName());
        department.setDepartmentDescription(departmentDto.getDepartmentDescription());
        department.setDepartmentCode(departmentDto.getDepartmentCode());
        return department;
    }
}
