package com.microservice.jobservice.job.mapper;

import com.microservice.jobservice.job.Job;
import com.microservice.jobservice.job.dto.JobWithCompanyDTO;
import com.microservice.jobservice.job.external.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(source = "job.id", target = "id")
    @Mapping(source = "job.description", target = "description")
    JobWithCompanyDTO mapToDto(Job job, Company company);
}
