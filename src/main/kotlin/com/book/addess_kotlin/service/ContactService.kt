package com.book.addess_kotlin.service

import com.book.addess_kotlin.domain.Contact
import com.book.addess_kotlin.domain.ContactRepository

class ContactService(private  val repo: ContactRepository) {
    fun create(contact: Contact): Contact {
        require(contact.firstName.isNotBlank()) { "First name must not be empty" }
        require(contact.lastName.isNotEmpty()) { "Last name must not be empty" }
        require(contact.addresses.isNotEmpty() || contact.phoneNumbers.isNotEmpty()) {
            "At least one address or phone number is required"
        }
        contact.addresses.forEach {
            require(it.street.isNotBlank()) { "Street name must not be empty" }
            require(it.houseNumber.isNotBlank()) { "House number must not be empty" }
        }
        contact.phoneNumbers.forEach {
            require(it.number.isNotBlank()) { "Phone number must not be empty" }
        }
        return repo.save(contact)
    }

    fun update(id: Long, updater: (Contact) -> Contact): Contact {
        val existing = repo.findById(id) ?: error("Contact not found with id $id")
        return repo.save(updater(existing).copy(id = id))
    }

    fun delete(id: Long) = repo.deleteById(id)

    fun list(): List<Contact> = repo.findAll()

    fun get(id: Long): Contact? = repo.findById(id)

    fun search(lastNamePrefix: String): List<Contact> = repo.searchByLastName(lastNamePrefix)

}