package org.yearup.data;

import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.math.BigDecimal;

public interface OrderDao {

    public void createOrder(Profile profile, ShoppingCart cart);

    public void addOrderToDatabase(int orderId, ShoppingCartItem item);
}
