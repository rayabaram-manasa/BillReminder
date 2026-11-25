package com.manasa.service;

import com.manasa.dto.UserRegistrationDTO;
import com.manasa.model.User;

public interface UserService {
	void registerUser(UserRegistrationDTO userRegistrationDTO);
    User findByEmail(String email);
}
