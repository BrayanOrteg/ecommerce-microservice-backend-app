package com.selimhorri.app.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductResource {
	
	private final ProductService productService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<ProductDto>> findAll() {
		log.info("*** ProductDto List, controller; fetch all categories *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.productService.findAll()));
	}
		
	@GetMapping("/{productId}")
	public ResponseEntity<ProductDto> findById(
			@PathVariable("productId") 
			@NotBlank(message = "Input must not be blank!") 
			@Valid final String productId) {
		log.info("*** ProductDto, resource; fetch product by id: {} *", productId);
		try {
			ProductDto product = this.productService.findById(Integer.parseInt(productId));
			if (product != null) {
				return ResponseEntity.ok(product);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (NumberFormatException e) {
			log.error("Invalid product ID format: {}", productId);
			return ResponseEntity.badRequest().build();
		} catch (ProductNotFoundException e) {
			log.error("Product not found: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("Error fetching product: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
		
	@PostMapping
	public ResponseEntity<ProductDto> save(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final ProductDto productDto) {
		log.info("*** ProductDto, resource; save product *");
		try {
			ProductDto savedProduct = this.productService.save(productDto);
			return ResponseEntity
					.created(null)  // Idealmente se debería incluir la URI del nuevo recurso
					.body(savedProduct);
		} catch (Exception e) {
			log.error("Error saving product: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
		
	@PutMapping
	public ResponseEntity<ProductDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final ProductDto productDto) {
		log.info("*** ProductDto, resource; update product *");
		try {
			if (productDto.getProductId() == null) {
				return ResponseEntity.badRequest().build();
			}
			
			ProductDto updatedProduct = this.productService.update(productDto);
			return ResponseEntity.ok(updatedProduct);
		} catch (ProductNotFoundException e) {
			log.error("Product not found: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("Error updating product: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
		
	@PutMapping("/{productId}")
	public ResponseEntity<ProductDto> update(
			@PathVariable("productId")
			@NotBlank(message = "Input must not be blank!")
			@Valid final String productId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final ProductDto productDto) {
		log.info("*** ProductDto, resource; update product with productId: {} *", productId);
		try {
			int id = Integer.parseInt(productId);
			ProductDto updatedProduct = this.productService.update(id, productDto);
			return ResponseEntity.ok(updatedProduct);
		} catch (NumberFormatException e) {
			log.error("Invalid product ID format: {}", productId);
			return ResponseEntity.badRequest().build();
		} catch (ProductNotFoundException e) {
			log.error("Product not found: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("Error updating product: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> deleteById(@PathVariable("productId") final String productId) {
		log.info("*** DELETE request, resource; delete product by id: {} *", productId);
		try {
			this.productService.deleteById(Integer.parseInt(productId));
			log.info("Successfully deleted product with ID: {}", productId);
			// Retornar 204 No Content para operaciones exitosas de borrado
			return ResponseEntity.noContent().build();
		} catch (NumberFormatException e) {
			log.error("Invalid product ID format: {}", productId);
			return ResponseEntity.badRequest().build();
		} catch (ProductNotFoundException e) {
			log.error("Product not found: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("Error deleting product: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
	
}










