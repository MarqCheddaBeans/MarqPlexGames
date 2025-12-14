package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource ds) {
        super(ds);
    }

    @Override
    public void checkout(Profile profile, ShoppingCart cart) {
        BigDecimal total = cart.getTotal();

       LocalDate today = LocalDate.now();

        try(Connection c = ds.getConnection();
            PreparedStatement q = c.prepareStatement("""
                    INSERT INTO Orders (user_id, date, address, city, state, zip, shipping_amount)
                    VALUES(?,?,?,?,?,?,?)
                    """)){
            q.setInt(1, profile.getUserId());
            q.setDate(2, Date.valueOf(today));
            q.setString(3, profile.getAddress());
            q.setString(4, profile.getCity());
            q.setString(5, profile.getState());
            q.setString(6, profile.getZip());
            q.setBigDecimal(7, total);

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error checking out " + e);
        }

    }
}
