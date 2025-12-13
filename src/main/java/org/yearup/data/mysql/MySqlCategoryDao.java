package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {


    public MySqlCategoryDao(DataSource ds)
    {
        super(ds);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        try(
                Connection c = ds.getConnection();
                PreparedStatement q = c.prepareStatement("""
                        SELECT category_id, name, description
                        FROM categories
                        """);
                ResultSet r = q.executeQuery();
                ){
            while(r.next()){
                Category cat = new Category();
                cat.setCategoryId(r.getInt("Category_id"));
                cat.setName(r.getString("Name"));
                cat.setDescription(r.getString("Description"));

                categories.add(cat);
            }
        }catch(SQLException e){
            System.out.println("Error getting all categories");
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        Category cat = new Category();

        try(Connection c = ds.getConnection();
        PreparedStatement q = c.prepareStatement("""
                SELECT 
                    category_id, name, description
                FROM 
                    categories
                WHERE 
                    category_id = ?
                """)){
            q.setInt(1,categoryId);

            ResultSet r = q.executeQuery();

            if(r.next()){
                cat.setCategoryId(r.getInt("Category_id"));
                cat.setName(r.getString("Name"));
                cat.setDescription(r.getString("Description"));
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }catch(SQLException e){
            System.out.println("Error getting category with id: " + categoryId);
        }
        return cat;
    }

    @Override
    public Category create(Category category) {

        try(Connection c = ds.getConnection();
        PreparedStatement q = c.prepareStatement("""
                INSERT INTO Categories(Name, Description) VALUES(?,?)
                """)){
            q.setString(1, category.getName());
            q.setString(2, category.getDescription());

            q.executeUpdate();
        }catch (SQLException e){
            System.out.println("Error adding category");
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category) {

        try(Connection c = ds.getConnection();
        PreparedStatement q = c.prepareStatement("""
                UPDATE
                    categories
                SET
                    Category_ID = COALESCE(?, Category_ID),
                    Name = COALESCE(?, Name),
                    Description = COALESCE(?, Description)                    
                WHERE
                    Category_ID = ?
                """)){
            if(category.getCategoryId() == null || category.getCategoryId() == 0){
                q.setNull(1, Types.INTEGER);
            }else{
                q.setInt(1, category.getCategoryId());
            }
            q.setString(2, category.getName());
            q.setString(3, category.getDescription());

            q.setInt(4, categoryId);

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error updating category");
        }

    }

    @Override
    public void delete(int categoryId) {
        try(Connection c = ds.getConnection();
        PreparedStatement q = c.prepareStatement("""
                DELETE FROM Categories
                WHERE Category_id = ?
                """)){
            q.setInt(1, categoryId);

            q.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error removing category" + e);
        }

    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category(){{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
