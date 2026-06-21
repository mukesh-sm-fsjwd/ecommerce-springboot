package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.model.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
public class CategoryController {

    List<Category> categories = new ArrayList<>();

    @GetMapping("public/categories")
    public List<Category> getCategories(){
        return categories;
    }

}
