
import controller.OrderController
import controller.ProductController
import controller.RiderController
import userInput.UserInputHandler

fun main() {
    val riderController = RiderController()

    val orderController = OrderController(riderController)

    val productController = ProductController(orderController,riderController)
    orderController.setProductController(productController)

    val userInputHandler = UserInputHandler(orderController, productController, riderController)
    while (true) {
        println("___________________________")
        println("===== Main Menu =====")
        println("1. Manage Products")
        println("2. Manage Riders")
        println("3. Place Order")
        println("4. View Order Delivery")
        println("5. Exit")
        print("Choose an option: ")
        when (readlnOrNull()?.toIntOrNull()) {
            1 -> userInputHandler.manageProducts()
            2 -> userInputHandler.manageRiders()
            3 -> userInputHandler.handleOrderInput()
            4 -> userInputHandler.handleOrderDelivery()
            5 -> break
            else -> println("Invalid option. Please try again.")
        }
    }
}