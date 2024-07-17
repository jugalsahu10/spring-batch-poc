package com.spring_batch.poc


import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Objects

@RestController
class JobService(
    private val jobLauncher: JobLauncher,
    private val job: Job
) {

    @GetMapping("/run")
    fun runJob(): ResponseEntity<Objects> {
        val jobParameters = JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(job, jobParameters)
        return ResponseEntity.ok().build()
    }
}
