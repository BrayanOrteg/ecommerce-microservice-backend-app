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

import com.selimhorri.app.business.user.model.UserDto;
import com.selimhorri.app.business.user.model.response.UserUserServiceCollectionDtoResponse;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "USER-SERVICE", contextId = "userClientService", path = "/user-service/api/users")
public interface UserClientService {
	
	@GetMapping
	@Retry(name = "userService")
	ResponseEntity<UserUserServiceCollectionDtoResponse> findAll();
	
	@GetMapping("/{userId}")
	@Retry(name = "userService")
	ResponseEntity<UserDto> findById(
			@PathVariable("userId") 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String userId);
	
	@GetMapping("/username/{username}")
	@Retry(name = "userService")
	ResponseEntity<UserDto> findByUsername(
			@PathVariable("username") 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String username);
	
	@PostMapping
	@Retry(name = "userService")
	ResponseEntity<UserDto> save(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final UserDto userDto);
	
	@PutMapping
	@Retry(name = "userService")
	ResponseEntity<UserDto> update(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final UserDto userDto);
	
	@PutMapping("/{userId}")
	@Retry(name = "userService")
	ResponseEntity<UserDto> update(
			@PathVariable("userId") 
			@NotBlank(message = "*Input must not blank!**") final String userId, 
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final UserDto userDto);
	
	@DeleteMapping("/{userId}")
	@Retry(name = "userService")
	ResponseEntity<Boolean> deleteById(@PathVariable("userId") @NotBlank(message = "*Input must not blank!**") @Valid final String userId);
	
}










