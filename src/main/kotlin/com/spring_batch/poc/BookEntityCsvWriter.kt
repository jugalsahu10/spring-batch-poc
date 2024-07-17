package com.spring_batch.poc

import com.spring_batch.poc.entity.Book
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStream
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.batch.item.file.transform.PassThroughLineAggregator
import org.springframework.core.io.FileSystemResource
import java.io.File


class BookEntityCsvWriter : ItemWriter<Book>, ItemStream {
    private var writer: FlatFileItemWriter<Book>

    init {
        initializeCsvFile()
        this.writer = FlatFileItemWriter<Book>()
        writer.setResource(
            FileSystemResource(CSV_FILE)
        )
        writer.setLineAggregator(
            object : DelimitedLineAggregator<Book>() {
                init {
                    setDelimiter(",")
                    setFieldExtractor(
                        object : BeanWrapperFieldExtractor<Book>() {
                            init {
                                setNames(
                                    arrayOf(
                                        "id", "author", "name",
                                        "price"
                                    )
                                )
                            }
                        })
                }
            })
    }

    private fun initializeCsvFile() {
        val file = File(CSV_FILE)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                throw RuntimeException(
                    "Error creating CSV file", e
                )
            }
        }
    }

    companion object {
        private const val CSV_FILE = "output.csv"
    }

    override fun open(executionContext: ExecutionContext) {
        writer.open(executionContext)
    }

    override fun write(chunk: Chunk<out Book>) {
        writer.write(chunk)
    }
}
