package com.book.addess_kotlin.domain

import java.time.LocalDate

data class Address(
    val id: Long? = null,
    val street: String,
    val houseNumber: String,
    val postalCode: String? = null,
    val city: String? = null,
    val type: AddressType
)

data class PhoneNumber(
    val id: Long? = null,
    val number: String,
    val kind: PhoneKind,
    val usage: UsageType
)

data class Contact(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate? = null,
    val addresses: List<Address> = emptyList(),
    val phoneNumbers: List<PhoneNumber> = emptyList()
)