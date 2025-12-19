package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.data.OrderDao;
import org.yearup.data.ProductDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

import java.math.BigDecimal;

// Like @Component, @Service registers the class as a Spring bean,but signals that it contains business logic (service layer).
@Service
public class CheckoutService {

    private ShoppingCartDao shoppingCartDao;
    private OrderDao orderDao;
    private ProductDao productDao;
    private ProfileDao profileDao;

    @Autowired
    public CheckoutService(ShoppingCartDao shoppingCartDao, OrderDao orderDao, ProductDao productDao, ProfileDao profileDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.profileDao = profileDao;
    }

    //Transactional ensures checkout process happens all together or none at all if any errors
    @Transactional
    public BigDecimal checkout(int userId){

        //Retrieve current users shoppingcart
        ShoppingCart cart = shoppingCartDao.getByUserId(userId);

        //Retrieve current users profile for their information
        Profile profile = profileDao.getProfileByUserID(userId);

        //Insert an order into the database with retrieved information, will return the orderId for us to add into next database
        int orderId = orderDao.createOrder(profile,cart);

        //Loop through cart items, reduce quantity for each product and reflect to databsae
        cart.getItems().values().forEach(item -> {
            orderDao.updateStock(item.getProductId(), item.getQuantity());
            orderDao.addOrderToDatabase(orderId, item);
        });

        //after purchase complete, clear the users cart
        shoppingCartDao.clearCart(userId);

        //return the total amount paid
        return cart.getTotal();
    }
}
