package org.yearup.data;

import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

public interface OrderDao {

    public void checkout(Profile profile, ShoppingCart cart);
}
