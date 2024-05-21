package com.locationvoiture.dto;

import com.locationvoiture.enums.UserRole;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private UserRole userRole;

}
