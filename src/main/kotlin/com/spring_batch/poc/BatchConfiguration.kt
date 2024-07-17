package com.spring_batch.poc


import com.spring_batch.poc.entity.Book
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*
import javax.sql.DataSource

@Configuration
@EnableBatchProcessing
class BatchConfiguration(
) {

    @Bean
    fun reader(entityManagerFactory: EntityManagerFactory?): ItemReader<Book> {
        val reader
                : JpaPagingItemReader<Book> = JpaPagingItemReader<Book>()
        reader.setEntityManagerFactory(
            entityManagerFactory!!
        )
        reader.setQueryString(
            "SELECT b FROM Book b"
        ) // Use the entity name
        // 'Book'
        reader.pageSize = 10
        return reader
    }

    @Bean
    fun processor(): ItemProcessor<Book, Book> {
        return BookEntityItemProcessor()
    }

    @Bean
    fun writer(): ItemWriter<Book> {
        return BookEntityCsvWriter()
    }

    @Bean
    fun exportJob(exportStep: Step?): Job {
        return JobBuilder("exportJob", jobRepository())
            .incrementer(RunIdIncrementer())
            .flow(exportStep!!)
            .end()
            .build()
    }

    @Bean
    fun exportJobLauncher(): JobLauncher {
        val taskExecutorJobLauncher = TaskExecutorJobLauncher()
        taskExecutorJobLauncher.setJobRepository(jobRepository())
        taskExecutorJobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        taskExecutorJobLauncher.afterPropertiesSet()
        return taskExecutorJobLauncher
    }

    @Bean
    fun exportStep(
        reader: ItemReader<Book?>,
        processor: ItemProcessor<Book?, Book?>,
        writer: ItemWriter<Book?>
    ): Step {
        return StepBuilder("exportStep", jobRepository())
            .chunk<Book, Book>(10, transactionManager())
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build()
    }

    @Bean
    fun entityManagerFactory(): EntityManagerFactory? {
        val emf = LocalContainerEntityManagerFactoryBean()
        emf.dataSource = dataSource()
        emf.setPackagesToScan("com.spring_batch.poc")
        emf.jpaVendorAdapter = HibernateJpaVendorAdapter()
        emf.setJpaProperties(jpaProperties())
        emf.afterPropertiesSet()
        return emf.getObject()
    }

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(
            "com.mysql.cj.jdbc.Driver"
        )
        dataSource.url = "jdbc:mysql://localhost:3306/books"
        dataSource.username = "root"
        dataSource.password = "root"
        return dataSource
    }

    @Bean
    fun jpaProperties(): Properties {
        val properties = Properties()
        properties.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.MySQLDialect"
        )
        return properties
    }

    @Bean
    fun jobRepository(): JobRepository {
        val factory = JobRepositoryFactoryBean().apply {
            setDataSource(dataSource())
            transactionManager = transactionManager()
            setIsolationLevelForCreate("ISOLATION_SERIALIZABLE")
            setTablePrefix("BATCH_")
        }
        factory.afterPropertiesSet()
        return factory.getObject()!!
    }

    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource())
    }
}
