package controller

import dataModel.FoodProduct
import dataModel.Rider


class RiderController {
    private val riders = mutableListOf<Rider>()
    private var nextRiderID = 1
    private val riderRatings = mutableMapOf<Int, MutableList<Float>>()


    fun addRider(rider: Rider): Rider {
        val newRider = rider.copy(riderID = nextRiderID++, status = "available", ratings = null)
        riders.add(newRider)
        return newRider
    }

    fun editRider(riderID: Int, newRider: Rider) {
        val index = riders.indexOfFirst { it.riderID == riderID }
        if (index != -1) {
            riders[index] = newRider
        }
    }

    fun deleteRider(riderID: Int) {
        riders.removeIf { it.riderID == riderID }
    }

    fun searchRider(riderID: Int): Rider? {
        return riders.find { it.riderID == riderID }
    }

    fun viewAllRiders(): List<Rider> {
        return riders
    }

    fun viewHighRatingsRider(): Rider? {
        return riders.maxByOrNull { it.ratings ?: 0f }
    }

    fun updateRiderStatus(riderID: Int, status: String) {
        val rider = riders.find { it.riderID == riderID }
        if (rider != null) {
            val updatedRider = rider.copy(status = status)
            editRider(riderID, updatedRider)
        }
    }

    fun updateRiderRating(riderID: Int, newRating: Float) {
        val ratingsList = riderRatings.getOrPut(riderID) { mutableListOf() }
        ratingsList.add(newRating)
        val updatedRating = ratingsList.average().toFloat()
        val rider = riders.find { it.riderID == riderID }
        if (rider != null) {
            val updatedRider = rider.copy(ratings = updatedRating)
            editRider(riderID, updatedRider)
        }
    }

   private fun getDeliveredProductsForRider(riderID: Int, orderController: OrderController): Map<FoodProduct, Int> {
        val allOrders = orderController.getOAllOrders()
        val productCounts = mutableMapOf<FoodProduct, Int>()

        for (order in allOrders) {
            if (order.riderID == riderID) {
                val product = order.itemID?.let { orderController.getProductController().searchProduct(it) }
                if (product != null) {
                    productCounts[product] = productCounts.getOrDefault(product, 0) + 1
                }
            }
        }

        return productCounts
    }

     fun getDeliveredProductsForAllRiders(orderController: OrderController): Map<Rider, Map<FoodProduct, Int>> {
        val allRiders = viewAllRiders()
        val riderProductMap = mutableMapOf<Rider, Map<FoodProduct, Int>>()

        for (rider in allRiders) {
            val deliveredProducts = rider.riderID?.let { getDeliveredProductsForRider(it, orderController) } ?: emptyMap()
            riderProductMap[rider] = deliveredProducts
        }

        return riderProductMap
    }
}
