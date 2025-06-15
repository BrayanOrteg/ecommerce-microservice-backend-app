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

import com.selimhorri.app.business.user.model.CredentialDto;
import com.selimhorri.app.business.user.model.response.CredentialUserServiceCollectionDtoResponse;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "USER-SERVICE", contextId = "credentialClientService", path = "/user-service/api/credentials", decode404 = true)
public interface CredentialClientService {
	
	@GetMapping
	@Retry(name = "userService")
	ResponseEntity<CredentialUserServiceCollectionDtoResponse> findAll();
	
	@GetMapping("/{credentialId}")
	@Retry(name = "userService")
	ResponseEntity<CredentialDto> findById(
			@PathVariable("credentialId") 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String credentialId);
	
	@GetMapping("/username/{username}")
	@Retry(name = "userService")
	ResponseEntity<CredentialDto> findByUsername(
			@PathVariable("username") 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String username);
	
	@PostMapping
	@Retry(name = "userService")
	ResponseEntity<CredentialDto> save(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final CredentialDto credentialDto);
	
	@PutMapping
	@Retry(name = "userService")
	ResponseEntity<CredentialDto> update(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final CredentialDto credentialDto);
	
	@PutMapping("/{credentialId}")
	@Retry(name = "userService")
	ResponseEntity<CredentialDto> update(
			@PathVariable("credentialId") 
			@NotBlank(message = "*Input must not blank!**") final String credentialId, 
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final CredentialDto credentialDto);
	
	@DeleteMapping("/{credentialId}")
	@Retry(name = "userService")
	ResponseEntity<Boolean> deleteById(@PathVariable("credentialId") @NotBlank(message = "*Input must not blank!**") @Valid final String credentialId);
	
}










