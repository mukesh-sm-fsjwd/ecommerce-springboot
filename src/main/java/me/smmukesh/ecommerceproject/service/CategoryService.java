package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.CategoryRequest;
import me.smmukesh.ecommerceproject.dto.response.CategoryResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     *
     * @param categoryRequest
     * @return CategoryRequest,
     * The Reason why we're using CategoryRequest return type is , we're
     * returing the same data because we're dealing with the same entites
     * rather than paginated reponses.
     */
    public CategoryRequest createCategory(CategoryRequest categoryRequest){
        Category category = modelMapper.map(categoryRequest,Category.class);
        Optional<Category> categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());

        if(categoryFromDB.isPresent()){
            throw new APIException("Category with the Name : "+categoryRequest.getCategoryName()+" is already Exists.");
        }

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryRequest.class);
    }

    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize){

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> allCategories = categoryPage.getContent();

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

    public CategoryRequest updateCategory(CategoryRequest updatedCategoryRequest,Long categoryId){
        Category category = modelMapper.map(updatedCategoryRequest,Category.class);

        Category Optionalcategory = categoryRepository.findById(categoryId).orElseThrow(()
                -> new ResourceNotFoundException("Category", "Catgory Id", categoryId));
        Optionalcategory.setCategoryName(category.getCategoryName());

        Category savedCategory = categoryRepository.save(Optionalcategory);

        return modelMapper.map(savedCategory,CategoryRequest.class);
    }

    public CategoryRequest deleteCategory(Long categoryId){

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Catgory Id", categoryId));
        categoryRepository.deleteById(categoryId);

        return modelMapper.map(category,CategoryRequest.class);
    }
}
