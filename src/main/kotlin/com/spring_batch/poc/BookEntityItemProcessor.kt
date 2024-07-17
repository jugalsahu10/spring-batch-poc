package com.spring_batch.poc

import com.spring_batch.poc.entity.Book
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component


@Component
class BookEntityItemProcessor : ItemProcessor<Book, Book> {
    @Throws(Exception::class)
    override fun process(item: Book): Book? {
        return item
    }
}
