package com.payflow.app.service;

import com.payflow.app.entity.User;

public interface UserService {
	User findByUsername(String username);
}
