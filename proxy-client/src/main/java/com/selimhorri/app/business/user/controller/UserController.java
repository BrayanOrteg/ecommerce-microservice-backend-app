package com.selimhorri.app.business.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selimhorri.app.business.user.model.UserDto;
import com.selimhorri.app.business.user.model.response.UserUserServiceCollectionDtoResponse;
import com.selimhorri.app.business.user.service.UserClientService;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserClientService userClientService;
	
	@Cacheable(value = "users")
	@GetMapping
	public ResponseEntity<UserUserServiceCollectionDtoResponse> findAll() {
		return ResponseEntity.ok(this.userClientService.findAll().getBody());
	}
	
	@Cacheable(value = "userById", key = "#userId")
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> findById(@PathVariable("userId") final String userId) {
		return ResponseEntity.ok(this.userClientService.findById(userId).getBody());
	}
	
	@Cacheable(value = "userByUsername", key = "#username")
	@GetMapping("/username/{username}")
	public ResponseEntity<UserDto> findByUsername(@PathVariable("username") final String username) {
		return ResponseEntity.ok(this.userClientService.findByUsername(username).getBody());
	}
	
	@PostMapping
	@CacheEvict(value = {"users", "userById", "userByUsername"}, allEntries = true)
	public ResponseEntity<UserDto> save(@RequestBody final UserDto userDto) {
		return ResponseEntity.ok(this.userClientService.save(userDto).getBody());
	}
	
	@PutMapping
	@CacheEvict(value = {"users", "userById", "userByUsername"}, allEntries = true)
	public ResponseEntity<UserDto> update(@RequestBody final UserDto userDto) {
		return ResponseEntity.ok(this.userClientService.update(userDto).getBody());
	}
	
	@PutMapping("/{userId}")
	@CacheEvict(value = {"users", "userById", "userByUsername"}, allEntries = true)
	public ResponseEntity<UserDto> update(@PathVariable("userId") final String userId, @RequestBody final UserDto userDto) {
		return ResponseEntity.ok(this.userClientService.update(userDto).getBody());
	}
	
	@DeleteMapping("/{userId}")
	@CacheEvict(value = {"users", "userById", "userByUsername"}, allEntries = true)
	public ResponseEntity<Boolean> deleteById(@PathVariable("userId") final String userId) {
		return ResponseEntity.ok(this.userClientService.deleteById(userId).getBody());
	}
	
	
	
}










