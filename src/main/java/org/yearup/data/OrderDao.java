package org.yearup.data;

import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.math.BigDecimal;

public interface OrderDao {

    int createOrder(Profile profile, ShoppingCart cart);

    void addOrderToDatabase(int orderId, ShoppingCartItem item);

    void updateStock(int productId, int quantity);

}
