package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

// convert this class to a REST controller
@RestController
// only logged in users should have access to these actions
@PreAuthorize("hasAnyRole('USER','ADMIN')")
@RequestMapping("/cart")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;
    private ProfileDao profileDao;
    private OrderDao orderDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao, ProfileDao profileDao, OrderDao orderDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;

    }

    @GetMapping("")
    // each method in this controller requires a Principal object as a parameter
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad." + e);
        }
    }

    // add a POST method to add a product to the cart - the url should be
    @PostMapping("/products/{id}")
    @ResponseStatus(value = HttpStatus.CREATED)
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    public ShoppingCart addToCart(Principal principal, @PathVariable int id){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        shoppingCartDao.addToCart(id, userId);

        return shoppingCartDao.getByUserId(userId);
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{id}")
    public ShoppingCart updateQuantity(Principal principal, @PathVariable int id, @RequestBody ShoppingCartItem item){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        shoppingCartDao.editCart(id, userId, item.getQuantity());

        return shoppingCartDao.getByUserId(userId);
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping("")
    public ShoppingCart clearCart(Principal principal){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userID = user.getId();

        shoppingCartDao.clearCart(userID);

        return new ShoppingCart();
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(Principal principal){
        User user = userDao.getByUserName(principal.getName());
        Profile profile = profileDao.getProfileByUserID(user.getId());
        ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());


        BigDecimal total = cart.getTotal();

        orderDao.checkout(profile,cart);

        shoppingCartDao.clearCart(user.getId());

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("total", total);
        receipt.put("message", "Checkout Successful!");

        return receipt;
    }
}
