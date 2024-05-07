package com.microservice.jobservice.job.impl;

import com.microservice.jobservice.job.Job;
import com.microservice.jobservice.job.JobRepository;
import com.microservice.jobservice.job.JobService;
import com.microservice.jobservice.job.dto.JobWithCompanyDTO;
import com.microservice.jobservice.job.external.Company;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
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
        RestTemplate restTemplate = new RestTemplate();

        Optional.ofNullable(jobs).ifPresent(jobDetail -> jobDetail.forEach(job -> {
            Company company = restTemplate.getForObject("http://localhost:8081/companies/" + job.getCompanyId(), Company.class);
            JobWithCompanyDTO dto = new JobWithCompanyDTO();
            dto.setJob(job);
            dto.setCompany(company);
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
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
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