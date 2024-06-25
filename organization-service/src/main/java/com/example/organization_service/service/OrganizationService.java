package com.example.organization_service.service;

import com.example.organization_service.dto.OrganizationDto;

public interface OrganizationService {

    OrganizationDto saveOrganization(OrganizationDto organizationDto);

    OrganizationDto getOrganizationById(Long id);

    OrganizationDto getOrganizationByCode(String organizationCode);

    void deleteOrganizationById(Long id);
}
