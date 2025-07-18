package com.selimhorri.app.business.user.service;

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

import com.selimhorri.app.business.user.model.AddressDto;
import com.selimhorri.app.business.user.model.response.AddressUserServiceCollectionDtoResponse;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "USER-SERVICE", contextId = "addressClientService", path = "/user-service/api/address", decode404 = true)
public interface AddressClientService {
	
	@GetMapping
	@Retry(name = "userService")
	ResponseEntity<AddressUserServiceCollectionDtoResponse> findAll();
	
	@GetMapping("/{addressId}")
	@Retry(name = "userService")
	ResponseEntity<AddressDto> findById(
			@PathVariable("addressId") 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String addressId);
	
	@PostMapping
	@Retry(name = "userService")
	ResponseEntity<AddressDto> save(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final AddressDto addressDto);
	
	@PutMapping
	@Retry(name = "userService")
	ResponseEntity<AddressDto> update(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final AddressDto addressDto);
	
	@PutMapping("/{addressId}")
	@Retry(name = "userService")
	ResponseEntity<AddressDto> update(
			@PathVariable("addressId") 
			@NotBlank(message = "*Input must not blank!**") final String addressId, 
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final AddressDto addressDto);
	
	@DeleteMapping("/{addressId}")
	@Retry(name = "userService")
	ResponseEntity<Boolean> deleteById(@PathVariable("addressId") @NotBlank(message = "*Input must not blank!**") @Valid final String addressId);
	
}










