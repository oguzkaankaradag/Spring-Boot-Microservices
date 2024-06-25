package com.example.organization_service.service.impl;

import com.example.organization_service.dto.OrganizationDto;
import com.example.organization_service.entity.Organization;
import com.example.organization_service.mapper.OrganizationMapper;
import com.example.organization_service.repository.OrganizationRepository;
import com.example.organization_service.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public OrganizationDto saveOrganization(OrganizationDto organizationDto) {
        Organization organization = OrganizationMapper.toEntity(organizationDto);
        Organization savedOrganization = organizationRepository.save(organization);
        return OrganizationMapper.toDto(savedOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDto getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return OrganizationMapper.toDto(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDto getOrganizationByCode(String organizationCode) {
        Organization organization = organizationRepository.findByOrganizationCode(organizationCode);
        if (organization == null) {
            throw new RuntimeException("Organization not found with code: " + organizationCode);
        }
        return OrganizationMapper.toDto(organization);
    }

    @Override
    @Transactional
    public void deleteOrganizationById(Long id) {
        organizationRepository.deleteById(id);
    }
}
