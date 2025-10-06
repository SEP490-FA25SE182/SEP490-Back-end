package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.UserAnalyticsResponse;
import com.sep.rookieservice.dto.UserRequest;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(String id);
    UserResponse getByEmail(String email);
    List<UserResponse> create(List<UserRequest> requests);
    UserResponse update(String id, UserRequest request);
    void softDelete(String id);
    Page<UserResponse> search(String gender, String roleId, IsActived isActived, Pageable pageable);
    UserAnalyticsResponse getAnalytics(Integer monthsBack);

}
