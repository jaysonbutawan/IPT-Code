package controller

import dataModel.*

class ProductController(private val orderController: OrderController,private val riderController: RiderController) {
    private val products = mutableListOf<FoodProduct>()
    private var nextProductID = 1



    fun addProduct(product: FoodProduct): FoodProduct {
        val newProduct = product.copy(itemID = nextProductID++)
        products.add(newProduct)
        return newProduct
    }

    fun editProduct(itemID: Int, newProduct: FoodProduct) {
        val index = products.indexOfFirst { it.itemID == itemID }
        if (index != -1) {
            products[index] = newProduct
        }
    }

    fun deleteProduct(itemID: Int) {
        products.removeIf { it.itemID == itemID }
    }

    fun searchProduct(itemID: Int): FoodProduct? {
        return products.find { it.itemID == itemID }
    }

    fun viewAllProducts(): List<FoodProduct> {
        return products
    }

    fun viewMostOrderedProduct(): FoodProduct? {
        val allOrders = orderController.getOAllOrders()

        if (allOrders.isEmpty()) {
            println("No orders have been placed yet.")
            return null
        }

        val orderCounts = allOrders.groupingBy { it.itemID }.eachCount()
        val mostOrderedItemID = orderCounts.maxByOrNull { it.value }?.key

        return products.find { it.itemID == mostOrderedItemID }
    }

    fun getTotalOrdersForProduct(itemID: Int): Int {
        val allOrders = orderController.getOAllOrders()
        return allOrders.count { it.itemID == itemID }
    }

    fun getRidersForProduct(itemID: Int): List<Rider> {
        val allOrders = orderController.getOAllOrders()
        val riderIDs = allOrders.filter { it.itemID == itemID }.mapNotNull { it.riderID }.toSet()
        return riderIDs.mapNotNull { riderController.searchRider(it) }
    }

    fun getRiderDeliveryCountsForProduct(itemID: Int): Map<Int, Int> {
        val allOrders = orderController.getOAllOrders()
        return allOrders.filter { it.itemID == itemID }
            .groupingBy { it.riderID ?: 0 }
            .eachCount()
    }



    fun viewMostOrderedProductPerCategory(category: String): FoodProduct? {
        val filteredProducts = products.filter { it.category == category }
        val allOrders = orderController.getOAllOrders()

        if (filteredProducts.isEmpty()) {
            println("No products found in the category: $category.")
            return null
        }

        val orderCounts = allOrders
            .filter { order -> filteredProducts.any { it.itemID == order.itemID } }
            .groupingBy { it.itemID }
            .eachCount()

        val mostOrderedItemID = orderCounts.maxByOrNull { it.value }?.key
        return filteredProducts.find { it.itemID == mostOrderedItemID }
    }

}
