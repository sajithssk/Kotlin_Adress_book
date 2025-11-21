package com.book.addess_kotlin.adapter.jpa

import com.book.addess_kotlin.domain.Contact
import com.book.addess_kotlin.domain.ContactRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
@Transactional
class ContactRepoJpaAdapter(
    private val jpa: ContactJpaRepository
) : ContactRepository {
    override fun save(contact: Contact): Contact =
        jpa.save(contact.toEntity()).toDomain()

    override fun findAll(): List<Contact> =
        jpa.findAll().map { it.toDomain() }

    override fun findById(id: Long): Contact? =
        jpa.findById(id).orElse(null)?.toDomain()

    override fun deleteById(id: Long) = jpa.deleteById(id)

    override fun searchByLastName(prefix: String): List<Contact> =
        jpa.searchByLastNamePrefix(prefix).map { it.toDomain() }

}