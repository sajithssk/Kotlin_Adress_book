package com.book.addess_kotlin.domain

interface ContactRepository {
    fun save(contact: Contact): Contact
    fun findAll(): List<Contact>
    fun findById(id: Long): Contact?
    fun deleteById(id: Long)
    fun searchByLastName(prefix: String): List<Contact>
}