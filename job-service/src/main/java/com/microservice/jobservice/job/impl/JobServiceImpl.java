package com.microservice.jobservice.job.impl;

import com.microservice.jobservice.job.Job;
import com.microservice.jobservice.job.JobRepository;
import com.microservice.jobservice.job.JobService;
import com.microservice.jobservice.job.client.CompanyClient;
import com.microservice.jobservice.job.client.ReviewClient;
import com.microservice.jobservice.job.dto.JobDTO;
import com.microservice.jobservice.job.external.Company;
import com.microservice.jobservice.job.external.Review;
import com.microservice.jobservice.job.mapper.JobMapper;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyClient companyClient;
    private final ReviewClient reviewClient;
    private final JobMapper jobMapper;

    public JobServiceImpl(JobRepository jobRepository,
                          CompanyClient companyClient,
                          ReviewClient reviewClient,
                          JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
        this.jobMapper = jobMapper;
    }

    /**
     * Get All Jobs
     *
     * @return List<Job>
     */
    @Override
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();

        Optional.ofNullable(jobs).ifPresent(jobDetail -> jobDetail.forEach(job -> {
            Company company = getCompanyDetailsById(job.getCompanyId());
            List<Review> reviews = getReviewsByCompanyId(job.getCompanyId());
            JobDTO dto = jobMapper.mapToDto(job, company, reviews);
            jobDTOS.add(dto);
        }));
        return jobDTOS;
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
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if (Objects.nonNull(job)) {
            Company company = getCompanyDetailsById(job.getCompanyId());
            List<Review> reviews = getReviewsByCompanyId(job.getCompanyId());
            return jobMapper.mapToDto(job, company, reviews);
        } else {
            throw new NotFoundException();
        }
    }

    private List<Review> getReviewsByCompanyId(Long companyId) {
        return reviewClient.getReviewsByCompanyId(companyId);
    }

    private Company getCompanyDetailsById(Long companyId) {
        return companyClient.getCompanyById(companyId);
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
