package userInput

import dataModel.*
import controller.*

class UserInputHandler(
    private val orderController: OrderController,
    private val productController: ProductController,
    private val riderController: RiderController = RiderController()
){

    private val productCategories = listOf("Appetizers", "Main Courses", "Side Dishes", "Beverages", "Desserts")
    private val locations = listOf("Apokon", "Canocotan", "La Filipina", "Mankilam", "San Miguel", "Visayan Village")


    fun manageProducts() {
        while (true) {
            println("___________________________")
            println("===== Product Management =====")
            println("1. Add Product")
            println("2. Edit Product")
            println("3. Delete Product")
            println("4. Search Product")
            println("5. View All Products")
            println("6. Most Ordered Product")
            println("7. Most Ordered Product per Category")
            println("8. Orders & Delivery Stats")
            println("9. Back to Main Menu")
            print("Choose an option: ")
            when (readlnOrNull()?.toIntOrNull()) {
                1 -> handleAddProduct()
                2 -> handleEditProduct()
                3 -> handleDeleteProduct()
                4 -> handleSearchProduct()
                5 -> handleViewAllProducts()
                6 -> handleViewMostOrderedProduct()
                7 -> handleViewMostOrderedProductPerCategory()
                8 -> handleDeliveryStatsMenu()
                9 ->break
                else -> println("Invalid option. Please try again.")
            }
        }
    }


    fun manageRiders() {
        while (true) {
            println("___________________________")
            println("===== Manage Riders =====")
            println("1. Add Rider")
            println("2. Edit Rider")
            println("3. Delete Rider")
            println("4. Search Rider")
            println("5. View All Riders")
            println("6. View High-Rated Rider")
            println("7. View Products Delivered by All Riders")
            println("8. Back to Main Menu")
            print("Choose an option: ")
            when (readlnOrNull()?.toIntOrNull()) {
                1 -> handleAddRider()
                2 -> handleEditRider()
                3 -> handleDeleteRider()
                4 -> handleSearchRider()
                5 -> handleViewAllRiders()
                6 -> handleViewHighRatingsRider()
                7 -> handleAllRidersDeliveredProducts()
                8 -> break
                else -> println("Invalid option. Please try again.")
            }
        }
    }
    fun handleDeliveryStatsMenu() {
        println("___________________________")
        println("===== Delivery Stats =====")
        println("1. Total Orders per Product")
        println("2. Rider Delivery Counts")
        println("3. Back")
        print("Choose an option: ")
        when (readlnOrNull()?.toIntOrNull()) {
            1 -> handleTotalOrdersForProduct()
            2 -> handleRiderDeliveryCountsForProduct()
            3 -> return
            else -> println("Invalid option. Please try again.")
        }
    }


    private fun handleAddProduct() {
        println("Enter product details:")

        var name: String?
        do {
            print("Name: ")
            name = readlnOrNull()?.trim()

            if (name.isNullOrEmpty()) {
                println("Product name cannot be empty.")
            } else if (productController.viewAllProducts().any { it.name.equals(name, ignoreCase = true) }) {
                println("This product with the name '$name' already added.")
                name = null
            }
        } while (name.isNullOrEmpty())
        var price: Double?
        do {
            print("Price: ")
            price = readlnOrNull()?.toDoubleOrNull()

            if (price == null || price <= 0) {
                println("Invalid price. The price must be greater than ₱0.")
            } else if (price > 10_000) {
                println("The price cannot exceed ₱10,000. Please enter a valid price.")
            }
        } while (price == null || price <= 0 || price > 10_000)

        println("Select a category:")
        productCategories.forEachIndexed { index, category ->
            println("${index + 1}. $category")
        }

        var category: String?
        do {
            print("Enter category number: ")
            val categoryNumber = readlnOrNull()?.toIntOrNull()

            if (categoryNumber != null && categoryNumber in 1..productCategories.size) {
                category = productCategories[categoryNumber - 1]
            } else {
                println("Invalid category selection. Please try again.")
                category = null
            }
        } while (category == null)
        val product = FoodProduct(name = name, price = price, category = category)
        val addedProduct = productController.addProduct(product)
        println("Product added successfully with ID: ${addedProduct.itemID}")
    }

    private fun handleEditProduct() {
        val products = productController.viewAllProducts()

        if (products.isEmpty()) {
            println("No products available.")
            return
        }
        println("\nAvailable Products:")
        for (product in products) {
            println("ProductID: ${product.itemID}")
            println("ProductName: ${product.name}")
            println("Category: ${product.category}")
            println("Price: ${product.price?:0.0}")
            println("_______________________________")

        }

        print("Enter product ID to edit: ")
        val itemID = readlnOrNull()?.toIntOrNull()

        if (itemID != null) {
            val product = productController.searchProduct(itemID)
            if (product != null) {
                println("Enter new product details (leave blank to keep current values):")

                print("New Name (${product.name}): ")
                val newName = readlnOrNull()?.takeIf { it.isNotBlank() } ?: product.name

                print("New Price (${product.price}): ")
                val newPrice = readlnOrNull()?.toDoubleOrNull() ?: product.price

                println("Select a new category or press Enter to keep '${product.category}': ")
                productCategories.forEachIndexed { index, category ->
                    println("${index + 1}. $category")
                }
                print("Enter category number: ")
                val categoryNumber = readlnOrNull()?.toIntOrNull()

                val newCategory = if (categoryNumber != null && categoryNumber in 1..productCategories.size) {
                    productCategories[categoryNumber - 1]
                } else {
                    product.category                }

                val updatedProduct = FoodProduct(itemID, newName, newPrice, newCategory)
                productController.editProduct(itemID, updatedProduct)

                println("Product updated successfully.")
            } else {
                println("Product not found.")
            }
        } else {
            println("Invalid input. Please enter a valid product ID.")
        }
    }




    private fun handleDeleteProduct() {
        val products = productController.viewAllProducts()

        if (products.isEmpty()) {
            println("No products available.")
            return
        }
        println("\nAvailable Products:")
        for (product in products) {
            println("ProductID: ${product.itemID}")
            println("ProductName: ${product.name}")
            println("Category: ${product.category}")
            println("Price: ${product.price?:0.0}")
            println("_______________________________")

        }

        if(productController.viewAllProducts().isEmpty()){
            println("No product found!")
            return
        }
        print("Enter product ID to delete: ")
        val itemID = readlnOrNull()?.toIntOrNull()
        if (itemID != null) {
            productController.deleteProduct(itemID)
            println("Product deleted successfully.")
        } else {
            println("Invalid input.")
        }
    }

    private fun handleSearchProduct() {
        if(productController.viewAllProducts().isEmpty()){
            println("No product found!")
            return
        }
        print("Enter product ID to search: ")
        val itemID = readlnOrNull()?.toIntOrNull()
        if (itemID != null) {
            val product = productController.searchProduct(itemID)
            if (product != null) {
                println("Product ID: ${product.itemID}")
                println("Product name: ${product.name}")
                println("Product Price: ${product.price}")
                println("Product Category: ${product.category}")
            } else {
                println("Product not found.")
            }
        } else {
            println("Invalid input.")
        }
    }

   private fun handleViewAllProducts() {
        val products = productController.viewAllProducts()

        if (products.isEmpty()) {
            println("No products available.")
            return
        }
        println("\nAvailable Products:")
        for (product in products) {
            println("")
            println("ProductID: ${product.itemID}")
            println("ProductName: ${product.name}")
            println("Category: ${product.category}")
            println("Price: ${product.price?:0.0}")
            println("_______________________________")

        }

    }

    private fun handleViewMostOrderedProduct() {
        val product = productController.viewMostOrderedProduct()
        if (product != null) {
            println("Most Ordered Product: ${product.name} \nCategory: ${product.category}")
        }
    }


    private fun handleViewMostOrderedProductPerCategory() {
        println("Select a category:")
        productCategories.forEachIndexed { index, category ->
            println("${index + 1}. $category")
        }
        print("Enter category number: ")
        val categoryNumber = readlnOrNull()?.toIntOrNull()

        if (categoryNumber != null && categoryNumber in 1..productCategories.size) {
            val category = productCategories[categoryNumber - 1]
            val product = productController.viewMostOrderedProductPerCategory(category)

            if (product != null) {
                println("Most Ordered Product in $category: ${product.name}")
            }else{
                println("No ordered product has been placed in $category")
            }
        }
    }

    private fun handleAddRider() {
        println("Enter rider details:")
        var riderName: String?
        do {
            print("Rider Name: ")
            riderName = readlnOrNull()?.trim()

            if (riderName.isNullOrEmpty()) {
                println("Rider name cannot be empty. Please enter a valid name.")
            } else if (riderController.viewAllRiders().any { it.riderName.equals(riderName, ignoreCase = true) }) {
                println("A rider with the name '$riderName' already exists. Please enter a different name.")
                return
            }
        } while (riderName.isNullOrEmpty())
        val rider = Rider(riderName = riderName)
        val addedRider = riderController.addRider(rider)

        println("Rider added successfully with ID: ${addedRider.riderID}")
    }


    private fun handleEditRider() {
        val riders = riderController.viewAllRiders()
        if(riders.isEmpty()){
            println("No rider found!")
            return
        }
        for(rider in riders) {
            println("RiderID: ${rider.riderID}")
            println("RiderName: ${rider.riderName}")
            println("--------------------------------")

        }
        print("Enter Rider ID to edit: ")
        val riderID = readlnOrNull()?.toIntOrNull()

        if (riderID == null) {
            println("Invalid input. Please enter a valid numeric Rider ID.")
            return
        }
        val rider = riderController.searchRider(riderID)

        if (rider == null) {
            println("Rider with ID $riderID not found.")
            return
        }
        var newName: String?
        do {
            print("Enter new Rider Name or press Enter to keep '${rider.riderName}': ")
            newName = readlnOrNull()?.trim()

            if (newName.isNullOrEmpty()) {
                newName = rider.riderName
                break
            }

            if (riderController.viewAllRiders().any { it.riderName.equals(newName, ignoreCase = true) }) {
                println("A rider with the name '$newName' already exists. Name will remain '${rider.riderName}'.")
                newName = rider.riderName
                return
            }
        } while (newName.isNullOrEmpty())
        val updatedRider = Rider(riderID, newName, rider.status, rider.ratings)

        riderController.editRider(riderID, updatedRider)
        println("Rider updated successfully.")
    }

    private  fun handleDeleteRider() {
        val riders = riderController.viewAllRiders()
        if(riders.isEmpty()){
            println("No rider found!")
            return
        }
        for(rider in riders) {
            println("RiderID: ${rider.riderID}")
            println("RiderName: ${rider.riderName}")
            println("--------------------------------")
        }
        print("Enter Rider ID to delete: ")
        val riderID = readlnOrNull()?.toIntOrNull()

        if (riderID == null) {
            println("Invalid input. Please enter a valid numeric Rider ID.")
            return
        }
        val riderExists = riders.any { it.riderID == riderID }

        if (!riderExists) {
            println("Rider with ID $riderID not found. Please enter a valid Rider ID.")
            return
        }
        riderController.deleteRider(riderID)
        println("Rider with ID $riderID has been deleted successfully.")
    }

    private fun handleSearchRider() {
        val riders = riderController.viewAllRiders()
        if(riders.isEmpty()){
            println("No rider found!")
            return
        }
        print("Enter rider ID to search: ")
        val riderID = readlnOrNull()?.toIntOrNull()
        if (riderID != null) {
            val rider = riderController.searchRider(riderID)
            if (rider != null) {
                println("Rider found: $rider")
            } else {
                println("Rider not found.")
            }
        } else {
            println("Invalid input.")
        }
    }

    private  fun handleViewAllRiders() {
        val riders = riderController.viewAllRiders()
        if (riders.isNotEmpty()) {
            println("All riders:")
            for(rider in riders){
                println("RiderID: ${rider.riderID}")
                println("RiderName: ${rider.riderName}")
                println("Status: ${rider.status}")
                println("Ratings: ${rider.ratings?: 0.0}")
                println("_______________________________")
            }
        } else {
            println("No riders found.")
        }
    }

    private fun handleViewHighRatingsRider() {
        val rider = riderController.viewHighRatingsRider()

        if (rider?.ratings != null) {
            println("Highest-rated rider")
            println("RiderID: ${rider.riderID}")
            println("RiderName: ${rider.riderName}")
            println("Status: ${rider.status}")
            println("Ratings: ${rider.ratings ?: 0.0}")
            println("_______________________________")
        } else {
            println("No delivery rider found with a rating yet.")
        }
    }

    fun handleOrderInput() {
        val rider = riderController.viewAllRiders()
        val products = productController.viewAllProducts()
        if (products.isEmpty()) {
            println("No products available. Please add products before placing an order.")
            return
        } else if(rider.isEmpty()){
            println("No rider available, PLease add rider before Placing an order.")
            return
        }
        for (product in products) {
            println("ProductID: ${product.itemID}")
            println("ProductName: ${product.name}")
            println("Price: ${product.price?:0.0}")
            println("_______________________________")
        }
        println("Enter order details:")
        print("Item ID: ")
        val itemID = readlnOrNull()?.toIntOrNull()

        if (itemID != null) {
            val product = productController.searchProduct(itemID)

            if (product == null) {
                println("Product with ID $itemID not found. Please enter a valid item ID.")
                return
            }

            println("Select a location:")
            locations.forEachIndexed { index, location ->
                println("${index + 1}. $location")
            }

            print("Enter location number: ")
            val locationNumber = readlnOrNull()?.toIntOrNull()

            if (locationNumber == null || locationNumber !in 1..locations.size) {
                println("Invalid location selection. Please try again.")
                return
            }

            val selectedLocation = locations[locationNumber - 1]
            val price = product.price ?: 0.0
            println("Product Price: $price")

            var paymentAmount: Double?
            do {
                print("Enter payment amount: ")
                paymentAmount = readlnOrNull()?.toDoubleOrNull()
                println()

                if (paymentAmount == null || paymentAmount <= 0) {
                    println("Invalid payment amount. Please enter an amount greater than 0.")
                } else if (paymentAmount < price) {
                    println("Insufficient payment. You must pay at least $price.")
                } else if (paymentAmount > price) {
                    println("Overpayment detected. Please enter the exact amount: $price.")
                }
            } while (paymentAmount == null || paymentAmount != price)

            val order = orderController.placeOrder(itemID, selectedLocation)

            if (order != null) {
                val deliveryTime = orderController.calculateDeliveryTime(selectedLocation)
                println("Order placed successfully with ID: ${order.orderID}")
                println("Estimated delivery time: $deliveryTime minutes")
            } else {
                println("Failed to place order. The item may not exist or no available riders.")
            }
        } else {
            println("Invalid input. Please enter a valid item ID.")
        }
    }


    fun handleOrderDelivery() {
        val orders = orderController.getOrders()

        if (orders.isEmpty()) {
            println("No orders available. No purchases have been made yet.")
            return
        }

        println("Pending Orders:")
        orders.forEach { order ->
            val itemName = order.itemID?.let { productController.searchProduct(it)?.name } ?: "Unknown Item"
            val riderName = riderController.searchRider(order.riderID ?: 0)?.riderName ?: "Unknown Rider"
            println("Order ID: ${order.orderID} | Item: $itemName | Rider: $riderName | Location: ${order.location} | Payment: ${order.paymentAmount}")
        }

        println("\nEnter order ID to mark as received:")
        val orderID = readlnOrNull()?.trim()?.toIntOrNull()

        if (orderID != null) {
            val order = orderController.viewOrderedDelivery(orderID)

            if (order != null) {
                val itemName = order.itemID?.let { productController.searchProduct(it)?.name } ?: "Unknown Item"
                val riderName = riderController.searchRider(order.riderID ?: 0)?.riderName ?: "Unknown Rider"

                println("Order Details:")
                println("Item Name: $itemName")
                println("Rider Name: $riderName")
                println("Location: ${order.location}")
                println("Payment Amount: ${order.paymentAmount}")

                println("Did you receive the order? (yes/no)")
                val received = readlnOrNull()?.lowercase()

                if (received == "yes") {
                    var rating: Int?
                    do {
                        println("Rate the rider (1-10):")
                        rating = readlnOrNull()?.toIntOrNull()
                        if (rating == null || rating !in 1..10) {
                            println("Invalid rating. Please enter a number between 1 and 10.")
                        }
                    } while (rating == null || rating !in 1..10)

                    orderController.markOrderReceived(orderID, rating)
                    orderController.removeOrder(orderID)
                } else {
                    orderController.markOrderNotReceived(orderID)
                }
            } else {
                println("Order not found. Please enter a valid order ID.")
            }
        } else {
            println("Invalid input. Please enter a valid order ID.")
        }
    }

    private fun handleTotalOrdersForProduct() {
        val products = productController.viewAllProducts()

        if (products.isEmpty()) {
            println("No products available. Please add products first.")
            return
        }

        println("\nTotal Orders for Each Product:")
        for (product in products) {
            val totalOrders = product.itemID?.let { productController.getTotalOrdersForProduct(it) } ?: 0
            println(" ID: ${product.itemID}  Product: ${product.name}  Total Orders: $totalOrders")
        }
    }

    private fun handleRiderDeliveryCountsForProduct() {
        val products = productController.viewAllProducts()

        if (products.isEmpty()) {
            println("No products available. Please add products first.")
            return
        }

        println("\nDelivery Counts by Rider for Each Product:")
        for (product in products) {
            val deliveryCounts = product.itemID?.let { productController.getRiderDeliveryCountsForProduct(it) }
            println("ID: ${product.itemID}  Product: ${product.name}")

            if (deliveryCounts != null && deliveryCounts.isNotEmpty()) {
                println("Riders and their delivery counts:")
                for ((riderID, count) in deliveryCounts) {
                    val rider = riderController.searchRider(riderID)
                    println("  - Rider ID: $riderID, Name: ${rider?.riderName ?: "Unknown"}, Deliveries: $count")
                }
            } else {
                println("  No deliveries have been made for this product yet.")
            }

            println("--------------------------------")
        }
    }

    private fun handleAllRidersDeliveredProducts() {
        val riderProductMap = riderController.getDeliveredProductsForAllRiders(orderController)

        if (riderProductMap.isEmpty()) {
            println("No riders available. Please add riders first.")
            return
        }

        println("\nProducts Delivered by All Riders:")
        for ((rider, deliveredProducts) in riderProductMap) {
            println("Rider ID: ${rider.riderID}  Rider: ${rider.riderName}")

            if (deliveredProducts.isNotEmpty()) {
                deliveredProducts.forEach { (product, count) ->
                    println("  Product ID: ${product.itemID} | Product: ${product.name} | Deliveries: $count")
                }
            } else {
                println("  No products have been delivered by this rider yet.")
            }
            println("--------------------------------")
        }
    }





}