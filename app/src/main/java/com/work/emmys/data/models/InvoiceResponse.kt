package com.work.emmys.data.models

data class InvoiceResponse(
    val action: String,
    val code: Int,
    val duration: Double,
    val message: String,
    val page: Int,
    val response: List<Response>,
    val resultsPerPage: Int,
    val status: Int,
    val subtotal: Double,
    val total: Double
) {
    data class Response(
        val branch: Branch,
        val container: Container,
        val cost: String,
        val createdAt: String,
        val date: String,
        val employee: Employee,
        val id: String,
        val invoiceDetails: List<InvoiceDetail>,
        val number: String,
        val oldID: Int,
        val paidRegion: String,
        val paidStatus: String,
        val balance: Double,
        val payment: Double,
        val `receiver`: Receiver,
        val sender: Sender,
        val user: User
    ) {
        data class Branch(
            val code: String,
            val id: Int,
            val name: String
        )

        data class Container(
            val id: Int,
            val name: String
        )

        class Employee

        data class InvoiceDetail(
            val barcodes: List<Barcode>,
            val createdAt: String,
            val id: String,
            val labels: Int,
            val name: String,
            val price: Double,
            val quantity: Int,
            val total: Double,
            val updatedAt: String
        ) {
            data class Barcode(
                val container: Container,
                val createdAt: String,
                val delivery: Delivery,
                val id: Int,
                val number: String,
                val scanDate: String,
                val status: Status,
                val updatedAt: String
            ) {
                data class Container(
                    val id: Int,
                    val name: String
                )

                data class Delivery(
                    val id: Int,
                    val name: String
                )

                data class Status(
                    val id: Int,
                    val name: String
                )
            }
        }

        data class Receiver(
            val address: Address,
            val customerType: Int,
            val id: String,
            val name: String,
            val phone1: String,
            val phone2: String
        ) {
            data class Address(
                val address1: String,
                val city: String,
                val apartment:String,
                val country: String,
                val state: String
            )
        }

        data class Sender(
            val address: Address,
            val customerType: Int,
            val apartment:String,
            val id: String,
            val name: String,
            val phone1: String,
            val phone2: String
        ) {
            data class Address(
                val address1: String,
                val address2: String,
                val apartment: String,
                val city: String,
                val country: String,
                val state: String,
                val zipcode: String
            )
        }

        data class User(
            val id: Int,
            val userName: String
        )
    }
}