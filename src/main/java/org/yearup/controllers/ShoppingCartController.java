package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.*;
import org.yearup.models.*;
import org.yearup.service.CheckoutService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

// convert this class to a REST controller
@RestController
// only logged in users should have access to these actions
@PreAuthorize("hasAnyRole('USER','ADMIN')")
@RequestMapping("cart")
@CrossOrigin
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;
    private ProfileDao profileDao;
    private OrderDao orderDao;
    //Handle checkout logic
    private CheckoutService checkoutService;

    //Autowire will automatically inject instances of these daos
    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao, ProfileDao profileDao, OrderDao orderDao,CheckoutService checkoutService) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;
        this.checkoutService = checkoutService;
    }

    //Gets the current users shoppingcart
    @GetMapping("")
    // each method in this controller requires a Principal object as a parameter
    public ShoppingCart getCart(Principal principal){
        try{
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

    //Adds a product to the cart
    @PostMapping("products/{id}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ShoppingCart addToCart(Principal principal, @PathVariable int id){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        shoppingCartDao.addToCart(id, userId);

        return shoppingCartDao.getByUserId(userId);
    }


    // updates the quantity of an item the cart
    @PutMapping("products/{id}")
    public ShoppingCart updateQuantity(Principal principal, @PathVariable int id, @RequestBody ShoppingCartItem item){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        shoppingCartDao.editCart(id, userId, item.getQuantity());

        return shoppingCartDao.getByUserId(userId);
    }

    //clears the users cart
    @DeleteMapping("")
    public ShoppingCart clearCart(Principal principal){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userID = user.getId();

        shoppingCartDao.clearCart(userID);

        return new ShoppingCart();
    }

    //endpoint to allow user to checkout
    @PostMapping("checkout")
    public Map<String, Object> checkout(Principal principal){

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);

        BigDecimal total = checkoutService.checkout(user.getId());

        Map<String, Object> output = new HashMap<>();

        output.put("total", total);

        return output;
    }
}
