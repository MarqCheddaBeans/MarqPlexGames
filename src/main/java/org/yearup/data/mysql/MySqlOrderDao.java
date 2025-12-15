package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    //Constructor to receive Datasource and allow connections to database
    public MySqlOrderDao(DataSource ds) {
        super(ds);
    }

    //Implements the checkout method from OrderDao interface
    @Override
    public void createOrder(Profile profile, ShoppingCart cart) {
        //Get total price of shoppingcart items for our order to checkout
       BigDecimal total = cart.getTotal();

       //get the current date
       LocalDate today = LocalDate.now();

        try(Connection c = ds.getConnection();
            PreparedStatement q = c.prepareStatement("""
                    INSERT INTO Orders (user_id, date, address, city, state, zip, shipping_amount)
                    VALUES(?,?,?,?,?,?,?)
                    """)){

            //Set values for the insert statements
            q.setInt(1, profile.getUserId());
            q.setDate(2, Date.valueOf(today));
            q.setString(3, profile.getAddress());
            q.setString(4, profile.getCity());
            q.setString(5, profile.getState());
            q.setString(6, profile.getZip());
            q.setBigDecimal(7, total);

            //Execute the injected query
            q.executeUpdate();
        }catch(SQLException e){
            //Handle errors
            System.out.println("Error checking out " + e);
        }

    }

    @Override
    public void addOrderToDatabase(int orderId, ShoppingCartItem item ){
        try(Connection c = ds.getConnection();
        PreparedStatement q = c.prepareStatement("""
                INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                VALUES (?,?,?,?,?)
                """) ){
            q.setInt(1, orderId);
            q.setInt(2, item.getProductId());
            q.setBigDecimal(3,item.getLineTotal());
            q.setInt(4, item.getQuantity());
            q.setBigDecimal(5, item.getDiscountPercent());

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error adding to database");
        }
    }
}
