package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.CategoryRequest;
import me.smmukesh.ecommerceproject.dto.response.CategoryResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,ModelMapper modelMapper){
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public String createCategory(Category category){
        Optional<Category> savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory.isPresent()){
            throw new APIException("Category with the Name : "+category.getCategoryName()+" is already Exists.");
        }
        categoryRepository.save(category);
        return "Category Added successfully!";
    }

    public CategoryResponse getAllCategories(){
        List<Category> allCategories = categoryRepository.findAll();
        if(allCategories.isEmpty()){
            throw new APIException("No Categories Added.");
        }

        List<CategoryRequest> categoryRequests = allCategories.stream()
                .map(category -> modelMapper.map(category,CategoryRequest.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryRequests);

        return categoryResponse;
    }

    public String updateCategory(Category updatedCategory,Long categoryId){
        Category Optionalcategory = categoryRepository.findById(categoryId).orElseThrow(()
                -> new ResourceNotFoundException("Category", "Catgory Id", categoryId));
        Optionalcategory.setCategoryName(updatedCategory.getCategoryName());
        categoryRepository.save(Optionalcategory);
        return "Category Updated !.";
    }

    public String deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Catgory Id", categoryId));
        categoryRepository.save(category);
        return "Category Updated Successfully!";
    }
}
