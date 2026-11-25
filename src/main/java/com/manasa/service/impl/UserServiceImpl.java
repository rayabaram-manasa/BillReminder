package com.manasa.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.manasa.dao.UserRepository;
import com.manasa.dto.UserRegistrationDTO;
import com.manasa.model.User;
import com.manasa.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
	@Override
	public void registerUser(UserRegistrationDTO userRegistrationDTO) {
		if(userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
			throw new IllegalArgumentException("An account with this email already exists.");
		}
		
		User user = User.builder()
				
				.fullName(userRegistrationDTO.getFullName())
                .email(userRegistrationDTO.getEmail())
                .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                .build();

        userRepository.save(user);
	}
	@Override
	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
		
	}
    
	
}
