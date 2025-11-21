package com.book.addess_kotlin.adapter.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ContactJpaRepository : JpaRepository<ContactEntity, Long> {
    @Query("select c from ContactEntity c where lower(c.lastName) like concat(lower(?1), '%') ")
    fun searchByLastNamePrefix(prefix: String): List<ContactEntity>
}