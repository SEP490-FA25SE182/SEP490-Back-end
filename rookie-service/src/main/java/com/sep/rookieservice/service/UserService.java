package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.UserRequest;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(String id);
    UserResponse getByEmail(String email);
    List<UserResponse> create(List<UserRequest> requests);
    UserResponse update(String id, UserRequest request);
    void softDelete(String id);
}
