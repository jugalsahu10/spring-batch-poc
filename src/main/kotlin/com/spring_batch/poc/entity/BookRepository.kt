package com.spring_batch.poc.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface BookEntityRepository : JpaRepository<Book, Int>
