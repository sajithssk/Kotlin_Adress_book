package com.book.addess_kotlin.config

import com.book.addess_kotlin.domain.ContactRepository
import com.book.addess_kotlin.service.ContactService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Beans {
    @Bean
    fun contactService(repo: ContactRepository) = ContactService(repo)
}
