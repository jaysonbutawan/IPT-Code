package controller

import dataModel.FoodProduct
import dataModel.Order
import dataModel.Rider


class OrderController(private val riderController: RiderController) {
    private val orders = mutableListOf<Order>()
    private var nextOrderID = 1
    private lateinit var productController: ProductController
    private val completedOrders = mutableListOf<Order>()


    fun setProductController(productController: ProductController) {
        this.productController = productController
    }

    fun getProductController(): ProductController {
        return productController
    }

    private val locationDistances = mapOf(
        "Apokon" to 5, "Canocotan" to 8, "La Filipina" to 10,
        "Mankilam" to 12, "San Miguel" to 7, "Visayan Village" to 6
    )

    fun getOAllOrders(): List<Order> {
        return orders + completedOrders
    }
    fun getOrders(): List<Order> {
        return orders
    }


    private fun selectProduct(itemID: Int): FoodProduct? {
        val product = productController.searchProduct(itemID)

        if (product == null) {
            println("Product with ID $itemID not found. Available products: ${productController.viewAllProducts()}")
        }

        return product
    }


    private fun selectRider(): Rider? {
        val availableRiders = riderController.viewAllRiders().filter { it.status == "available" }

        if (availableRiders.isEmpty()) {
            println("No available riders. Please try again later.")
            return null
        }

        return availableRiders.firstOrNull()
    }

    fun removeOrder(orderID: Int) {
        val order = orders.find { it.orderID == orderID }
        if (order != null) {
            completedOrders.add(order)
            orders.remove(order)
            println("Order ID $orderID has been marked as completed and removed from active orders.")
        }
    }

    fun calculateDeliveryTime(location: String): Int {
        val distance = locationDistances[location] ?: 10
        return distance * 5
    }

    private fun makePayment(order: Order): Boolean {
        orders.add(order)
        return true
    }

    private fun printReceipt(order: Order) {
        println("Order ID: ${order.orderID}")
        println("Item ID: ${order.itemID}")
        println("Rider ID: ${order.riderID}")
        println("Location: ${order.location}")
        println("Payment Amount: ${order.paymentAmount}")
    }

    fun viewOrderedDelivery(orderID: Int): Order? {
        return orders.find { it.orderID == orderID }
    }

    fun markOrderReceived(orderID: Int, rating: Int) {
        val order = orders.find { it.orderID == orderID }
        if (order != null) {
            val rider = riderController.searchRider(order.riderID ?: 0)
            if (rider != null) {
                riderController.updateRiderRating(rider.riderID ?: 0, rating.toFloat())
                riderController.updateRiderStatus(rider.riderID ?: 0, "available")
                println("Thank you for your feedback! Rider ${rider.riderName} is now available for new orders.")
            }
        }
    }

    fun markOrderNotReceived(orderID: Int) {
        val order = orders.find { it.orderID == orderID }
        if (order != null) {
            println("Just wait a minute, we are working with our couriers.")
        }
    }

    fun placeOrder(itemID: Int, location: String): Order? {
        val product = selectProduct(itemID)

        if (product == null) {
            println("Order failed: The item with ID $itemID is not found in the product list.")
            return null
        }
        println("Checking available riders...")
        val rider = selectRider()

        if (rider == null) {
            println("Order failed: No available riders at the moment. Please try again later.")
            return null
        }

        val deliveryTime = calculateDeliveryTime(location)
        val paymentAmount = product.price ?: 0.0
        val order = Order(nextOrderID++, itemID, rider.riderID, location, paymentAmount)

        if (makePayment(order)) {
            riderController.updateRiderStatus(rider.riderID ?: 0, "not available")
            printReceipt(order)
            println("Order placed successfully! Estimated delivery time: $deliveryTime minutes")
            return order
        }
        println("Order could not be completed due to an unknown issue.")
        return null
    }
}