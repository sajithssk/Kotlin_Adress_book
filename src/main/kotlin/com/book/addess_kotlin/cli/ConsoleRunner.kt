package com.book.addess_kotlin.cli

import com.book.addess_kotlin.domain.*
import com.book.addess_kotlin.service.ContactService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ConsoleRunner(private val service: ContactService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("Address Book CLI. Commands: add, edit, delete, list, show, search, help, exit")
        val input = generateSequence { readlnOrNull()?.trim() }
        for (line in input) {
            if (line.isBlank()) continue
            try {
                when (line.lowercase()) {
                    "add" -> addContact()
                    "edit" -> editContact()
                    "delete" -> deleteContact()
                    "list" -> listContacts()
                    "show" -> showContact()
                    "search" -> search()
                    "help" -> println("add | edit | delete | list | show | search | exit")
                    "exit" -> {
                        println("Bye.")
                        return
                    }
                    else -> println("Unknown command.")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    private fun addContact() {
        val first = prompt("First name (required)")
        val last = prompt("Last name (required)")
        val birth = prompt("Birth date (yyyy-MM-dd, optional)")
            .takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }

        val addresses = mutableListOf<Address>()
        val phones = mutableListOf<PhoneNumber>()

        while (addresses.isEmpty() && phones.isEmpty()) {
            if (yes("Add address? (y/n)")) {
                do {
                    val street = prompt("Street (required)")
                    val house = prompt("House number (required)")
                    val postal = prompt("Postal code (optional)")
                    val city = prompt("City (optional)")
                    val type = pick("Address type", AddressType.entries.toTypedArray())
                    addresses += Address(
                        street = street,
                        houseNumber = house,
                        postalCode = postal.ifBlank { null },
                        city = city.ifBlank { null },
                        type = type
                    )
                } while (yes("Another address? (y/n)"))
            }
            if (yes("Add phone? (y/n)")) {
                do {
                    val num = prompt("Phone number (required)")
                    val kind = pick("Kind", PhoneKind.entries.toTypedArray())
                    val usage = pick("Usage", UsageType.entries.toTypedArray())
                    phones += PhoneNumber(number = num, kind = kind, usage = usage)
                } while (yes("Another phone? (y/n)"))
            }
            if (addresses.isEmpty() && phones.isEmpty()) {
                println("Error: At least one address or phone number is required. Please add one.")
            }
        }

        runCatching {
            val saved = service.create(
                Contact(
                    firstName = first,
                    lastName = last,
                    birthDate = birth,
                    addresses = addresses,
                    phoneNumbers = phones
                )
            )
            println("Created contact id=${saved.id}")
        }.onFailure { println("Error: ${it.message}") }
    }

    private fun editContact() {
        val id = prompt("Contact id").toLongOrNull() ?: return println("Invalid id")
        val existing = service.get(id) ?: return println("Not found")
        println("Editing ${existing.firstName} ${existing.lastName}")
        val newFirst = prompt("First name [${existing.firstName}]").ifBlank { existing.firstName }
        val newLast = prompt("Last name [${existing.lastName}]").ifBlank { existing.lastName }
        runCatching {
            val updated = service.update(id) { it.copy(firstName = newFirst, lastName = newLast) }
            println("Updated id=${updated.id}")
        }.onFailure { println("Error: ${it.message}") }
    }

    private fun deleteContact() {
        val id = prompt("Contact id").toLongOrNull() ?: return println("Invalid id")
        runCatching { service.delete(id); println("Deleted.") }
            .onFailure { println("Error: ${it.message}") }
    }

    private fun listContacts() {
        service.list().forEach {
            println("[${it.id}] ${it.lastName}, ${it.firstName} | addresses=${it.addresses.size} phones=${it.phoneNumbers.size}")
        }
    }

    private fun showContact() {
        val id = prompt("Contact id").toLongOrNull() ?: return println("Invalid id")
        val c = service.get(id) ?: return println("Not found")
        println("Contact ${c.id}: ${c.firstName} ${c.lastName} birth=${c.birthDate ?: '-'}")
        c.addresses.forEach {
            println("  Address: ${it.type} ${it.street} ${it.houseNumber} ${it.postalCode ?: ""} ${it.city ?: ""}")
        }
        c.phoneNumbers.forEach {
            println("  Phone: ${it.kind}/${it.usage} ${it.number}")
        }
    }

    private fun search() {
        val prefix = prompt("Last name prefix")
        service.search(prefix).forEach {
            println("[${it.id}] ${it.lastName}, ${it.firstName}")
        }
    }

    private fun prompt(label: String): String {
        print("$label: ")
        return readlnOrNull()?.trim().orEmpty()
    }

    private fun yes(label: String): Boolean = prompt(label).lowercase() == "y"

    private fun <T : Enum<T>> pick(label: String, values: Array<T>): T {
        val map = values.associateBy { it.name.lowercase() }
        while (true) {
            val joined = values.joinToString("/") { it.name.lowercase() }
            val v = prompt("$label ($joined)")
            map[v.lowercase()]?.let { return it }
            println("Invalid. Try again.")
        }
    }
}
