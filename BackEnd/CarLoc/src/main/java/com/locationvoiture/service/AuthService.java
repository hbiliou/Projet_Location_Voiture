package com.locationvoiture.service;

import com.locationvoiture.dto.SignupRequest;
import com.locationvoiture.dto.UserDto;

public interface AuthService {
    UserDto createCustomer(SignupRequest signupRequest);
    boolean hasCustomerWithEmail(String email);


}
