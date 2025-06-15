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

import com.selimhorri.app.business.product.model.ProductDto;
import com.selimhorri.app.business.product.model.response.ProductProductServiceCollectionDtoResponse;
import com.selimhorri.app.business.product.service.ProductClientService;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductClientService productClientService;
	
	@Cacheable(value = "products")
	@GetMapping
	public ResponseEntity<ProductProductServiceCollectionDtoResponse> findAll() {
		return ResponseEntity.ok(this.productClientService.findAll().getBody());
	}
	
	@Cacheable(value = "productById", key = "#productId")
	@GetMapping("/{productId}")
	public ResponseEntity<ProductDto> findById(@PathVariable("productId") final String productId) {
		return ResponseEntity.ok(this.productClientService.findById(productId).getBody());
	}
		@PostMapping
	@CacheEvict(value = {"products", "productById"}, allEntries = true)
	public ResponseEntity<ProductDto> save(@RequestBody final ProductDto productDto) {
		ResponseEntity<ProductDto> response = this.productClientService.save(productDto);
		return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
	}
	
	@PutMapping
	@CacheEvict(value = {"products", "productById"}, allEntries = true)
	public ResponseEntity<ProductDto> update(@RequestBody final ProductDto productDto) {
		return ResponseEntity.ok(this.productClientService.update(productDto).getBody());
	}
	
	@PutMapping("/{productId}")
	@CacheEvict(value = {"products", "productById"}, allEntries = true)
	public ResponseEntity<ProductDto> update(@PathVariable("productId") final String productId, 
			@RequestBody final ProductDto productDto) {
		return ResponseEntity.ok(this.productClientService.update(productId, productDto).getBody());
	}	
	
	@DeleteMapping("/{productId}")
	@CacheEvict(value = {"products", "productById"}, allEntries = true)
	public ResponseEntity<Void> deleteById(@PathVariable("productId") final String productId) {
		this.productClientService.deleteById(productId);
		return ResponseEntity.noContent().build();
	}
	
	
	
}










