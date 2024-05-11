package com.microservice.jobservice.job.mapper;

import com.microservice.jobservice.job.Job;
import com.microservice.jobservice.job.dto.JobDTO;
import com.microservice.jobservice.job.external.Company;
import com.microservice.jobservice.job.external.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(source = "job.id", target = "id")
    @Mapping(source = "job.description", target = "description")
    JobDTO mapToDto(Job job, Company company, List<Review> reviews);
}
