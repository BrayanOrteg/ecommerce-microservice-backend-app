package com.selimhorri.app.business.product.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.selimhorri.app.business.product.model.CategoryDto;
import com.selimhorri.app.business.product.model.response.CategoryProductServiceCollectionDtoResponse;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "PRODUCT-SERVICE", contextId = "categoryClientService", path = "/product-service/api/categories")
public interface CategoryClientService {
	
	@GetMapping
	@Retry(name = "productService")
	ResponseEntity<CategoryProductServiceCollectionDtoResponse> findAll();
	
	@GetMapping("/{categoryId}")
	@Retry(name = "productService")
	ResponseEntity<CategoryDto> findById(
			@PathVariable("categoryId") 
			@NotBlank(message = "Input must not be blank!") 
			@Valid final String categoryId);
	
	@PostMapping
	@Retry(name = "productService")
	ResponseEntity<CategoryDto> save(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final CategoryDto categoryDto);
	
	@PutMapping
	@Retry(name = "productService")
	ResponseEntity<CategoryDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final CategoryDto categoryDto);
	
	@PutMapping("/{categoryId}")
	@Retry(name = "productService")
	ResponseEntity<CategoryDto> update(
			@PathVariable("categoryId")
			@NotBlank(message = "Input must not be blank!")
			@Valid final String categoryId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final CategoryDto categoryDto);
	
	@DeleteMapping("/{categoryId}")
	@Retry(name = "productService")
	ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId);
	
}










