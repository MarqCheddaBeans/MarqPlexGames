package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    protected static Product mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String subCategory = row.getString("subcategory");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, subCategory, stock, isFeatured, imageUrl);
    }
}
