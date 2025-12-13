package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController
@RequestMapping("categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    // create an Autowired controller to inject the categoryDao and ProductDao

    @Autowired
    public CategoriesController(CategoryDao catDao, ProductDao prodDao){
        this.categoryDao = catDao;
        this.productDao = prodDao;
    }


    @GetMapping("")
    public List<Category> getAll() {
        return categoryDao.getAllCategories();
    }

    @GetMapping("{id}")
    public Category getById(@PathVariable int id) {
        return categoryDao.getById(id);
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        // get a list of product by categoryId
        return productDao.listByCategoryId(categoryId);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addCategory(@RequestBody Category category){
        categoryDao.create(category);
    }

    @PutMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category){
        categoryDao.update(id, category);
    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id){
        categoryDao.delete(id);
    }
}
