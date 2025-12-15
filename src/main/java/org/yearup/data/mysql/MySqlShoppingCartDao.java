package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource ds) {
        super(ds);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        try(Connection c = ds.getConnection();
            PreparedStatement q = c.prepareStatement("""
                    SELECT user_id, S.product_id, quantity, P.*
                    FROM shopping_cart S
                    JOIN products P ON (P.product_id = S.Product_ID)
                    WHERE user_ID = ?
                    """)){
            q.setInt(1, userId);
            ResultSet r = q.executeQuery();

            while(r.next()){
                Product product = new Product();
                int quantity = r.getInt("quantity");

                product.setProductId(r.getInt("product_id"));
                product.setName(r.getString("name"));
                product.setPrice(r.getBigDecimal("price"));
                product.setCategoryId(r.getInt("category_id"));
                product.setDescription(r.getString("description"));
                product.setSubCategory(r.getString("subcategory"));
                product.setStock(r.getInt("stock"));
                product.setFeatured(r.getBoolean("featured"));
                product.setImageUrl(r.getString("image_url"));

                ShoppingCartItem cartItem = new ShoppingCartItem(product, userId, quantity);

                cart.add(cartItem);
            }
        }catch(SQLException e){
            System.out.println("Error getting cart by userid " + e);
        }
        return cart;
    }

    @Override
    public void addToCart(int productId, int userID) {
        try(
                Connection c = ds.getConnection();
                //prepare statement to UPSERT product to cart accounting for multiple of same products
                PreparedStatement q = c.prepareStatement("""
                        INSERT INTO shopping_cart (user_id, product_id,quantity) VALUES (?,?,1)
                        ON DUPLICATE KEY UPDATE quantity = quantity + 1
                        """)
                ){
            q.setInt(1, userID);
            q.setInt(2, productId);

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error adding to cart" + e);
        }
    }

    @Override
    public void clearCart(int userID) {
        try(
                Connection c = ds.getConnection();
                PreparedStatement q = c.prepareStatement("""
                        DELETE FROM shopping_cart
                        WHERE user_id = ?
                        """)
                ){
            q.setInt(1, userID);

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error clearing cart " + e);
        }
    }

    @Override
    public void editCart(int productID, int userID, int quantity) {
        //1st query to account for multiple of same item in cart update quantity
        try(
                Connection c = ds.getConnection();
                PreparedStatement q = c.prepareStatement("""
                        UPDATE shopping_cart
                        SET quantity = ?
                        WHERE user_id = ?
                        AND product_id = ?
                        AND quantity >= 1
                        """)
                ){
            q.setInt(1,quantity);
            q.setInt(2, userID);
            q.setInt(3, productID);

            q.executeUpdate();

            //After updating, if quantity is 0, remove product from cart
            if(quantity == 0){
                try (
                        PreparedStatement rQ = c.prepareStatement("""
                                DELETE FROM shopping_cart
                                WHERE quantity = 0
                                """)
                        ){
                    rQ.executeUpdate();

                }catch(SQLException e){
                    System.out.println("Error removing item" + e);
                }
            }

        }catch(SQLException e){
            System.out.println("Error removing from cart" + e);
        }
    }

}
