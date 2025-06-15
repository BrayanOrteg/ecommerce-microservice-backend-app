package com.selimhorri.app.business.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selimhorri.app.business.product.model.CategoryDto;
import com.selimhorri.app.business.product.model.response.CategoryProductServiceCollectionDtoResponse;
import com.selimhorri.app.business.product.service.CategoryClientService;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryClientService categoryClientService;
	
	@Cacheable(value = "categories")
	@GetMapping
	public ResponseEntity<CategoryProductServiceCollectionDtoResponse> findAll() {
		return ResponseEntity.ok(this.categoryClientService.findAll().getBody());
	}
	
	@Cacheable(value = "categoryById", key = "#categoryId")
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> findById(@PathVariable("categoryId") final String categoryId) {
		return ResponseEntity.ok(this.categoryClientService.findById(categoryId).getBody());
	}
	
	@PostMapping
	@CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
	public ResponseEntity<CategoryDto> save(@RequestBody final CategoryDto categoryDto) {
		return ResponseEntity.ok(this.categoryClientService.save(categoryDto).getBody());
	}
	
	@PutMapping
	@CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
	public ResponseEntity<CategoryDto> update(@RequestBody final CategoryDto categoryDto) {
		return ResponseEntity.ok(this.categoryClientService.update(categoryDto).getBody());
	}
	
	@PutMapping("/{categoryId}")
	@CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
	public ResponseEntity<CategoryDto> update(@PathVariable("categoryId") final String categoryId, 
			@RequestBody final CategoryDto categoryDto) {
		return ResponseEntity.ok(this.categoryClientService.update(categoryId, categoryDto).getBody());
	}
	
	@DeleteMapping("/{categoryId}")
	@CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
	public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
		return ResponseEntity.ok(this.categoryClientService.deleteById(categoryId).getBody());
	}
	
	
	
}










