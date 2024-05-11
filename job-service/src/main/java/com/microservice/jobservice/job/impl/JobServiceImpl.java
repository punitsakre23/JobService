package com.microservice.jobservice.job.impl;

import com.microservice.jobservice.job.Job;
import com.microservice.jobservice.job.JobRepository;
import com.microservice.jobservice.job.JobService;
import com.microservice.jobservice.job.dto.JobWithCompanyDTO;
import com.microservice.jobservice.job.external.Company;
import com.microservice.jobservice.job.mapper.JobMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RestTemplate restTemplate;
    private final JobMapper jobMapper;

    public JobServiceImpl(JobRepository jobRepository, RestTemplate restTemplate, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.restTemplate = restTemplate;
        this.jobMapper = jobMapper;
    }

    /**
     * Get All Jobs
     *
     * @return List<Job>
     */
    @Override
    public List<JobWithCompanyDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobWithCompanyDTO> jobWithCompanyDTOs = new ArrayList<>();

        Optional.ofNullable(jobs).ifPresent(jobDetail -> jobDetail.forEach(job -> {
            Company company = restTemplate.getForObject("http://COMPANY-SERVICE:8081/companies/" + job.getCompanyId(), Company.class);
            JobWithCompanyDTO dto = jobMapper.mapToDto(job, company);
            jobWithCompanyDTOs.add(dto);
        }));

        return jobWithCompanyDTOs;
    }

    /**
     * Create A Job
     */
    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    /**
     * Get Job By Job id
     *
     * @param id job id
     * @return Job
     */
    @Override
    public JobWithCompanyDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if (Objects.nonNull(job)) {
            Company company = restTemplate.getForObject("http://COMPANY-SERVICE:8081/companies/" + job.getCompanyId(), Company.class);
            return jobMapper.mapToDto(job, company);
        }
        return null;
    }

    /**
     * Delete Job By Job id
     *
     * @param id job id
     * @return boolean
     */
    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param id Job id
     * @param job Job
     * @return boolean
     */
    @Override
    public boolean updateJobById(Long id, Job job) {
        Optional<Job> res = jobRepository.findById(id);
        if (res.isPresent()) {
            updateJobDetails(res.get(), job);
            return true;
        }
        return false;
    }

    private void updateJobDetails(Job jobDetail, Job job) {
        if (job.getTitle() != null) {
            jobDetail.setTitle(job.getTitle());
        }
        if (job.getDescription() != null) {
            jobDetail.setDescription(job.getDescription());
        }
        if (job.getMinSalary() != null) {
            jobDetail.setMinSalary(job.getMinSalary());
        }
        if (job.getMaxSalary() != null) {
            jobDetail.setMaxSalary(job.getMaxSalary());
        }
        if (job.getLocation() != null) {
            jobDetail.setLocation(job.getLocation());
        }
        jobRepository.save(jobDetail);
    }

}
