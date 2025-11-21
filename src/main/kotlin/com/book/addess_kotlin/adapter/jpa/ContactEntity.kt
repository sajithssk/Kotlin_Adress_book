package com.book.addess_kotlin.adapter.jpa

import com.book.addess_kotlin.domain.*
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "contacts")
class ContactEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var firstName: String,
    var lastName: String,
    var birthDate: LocalDate? = null,
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var addresses: MutableList<AddressEntity> = mutableListOf(),
    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var phoneNumbers: MutableList<PhoneNumberEntity> = mutableListOf()
){
    constructor() : this(0, "", "", null)
}

@Entity
@Table(name = "addresses")
class AddressEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var street: String,
    var houseNumber: String,
    var postalCode: String? = null,
    var city: String? = null,
    @Enumerated(EnumType.STRING)
    var type: AddressType,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "contact_id")
    var contact: ContactEntity? = null
){
    constructor() : this(null, "", "", null, null, AddressType.PRIVATE, null)
}

@Entity
@Table(name = "phone_numbers")
class PhoneNumberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var number: String,
    @Enumerated(EnumType.STRING)
    var kind: PhoneKind,
    @Enumerated(EnumType.STRING)
    var usage: UsageType,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "contact_id")
    var contact: ContactEntity? = null
){
    constructor() : this(null, "", PhoneKind.LANDLINE, UsageType.PRIVATE, null)
}

fun ContactEntity.toDomain(): Contact = Contact(
    id = id,
    firstName = firstName,
    lastName = lastName,
    birthDate = birthDate,
    addresses = addresses.map {
        Address(it.id, it.street, it.houseNumber, it.postalCode, it.city, it.type)
    },
    phoneNumbers = phoneNumbers.map {
        PhoneNumber(it.id, it.number, it.kind, it.usage)
    }
)

fun Contact.toEntity(): ContactEntity {
    val ce = ContactEntity(id, firstName, lastName, birthDate)
    ce.addresses = addresses.map {
        AddressEntity(it.id, it.street, it.houseNumber, it.postalCode, it.city, it.type, ce)
    }.toMutableList()
    ce.phoneNumbers = phoneNumbers.map {
        PhoneNumberEntity(it.id, it.number, it.kind, it.usage, ce)
    }.toMutableList()
    return ce
}
