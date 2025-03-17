package dataModel

data class Order(
    val orderID: Int? = null,
    val itemID: Int? = null,
    val riderID: Int?= null,
    val location: String?= null,
    val paymentAmount: Double? = null
)