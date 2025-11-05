package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class UserAddressRequest {
    @NotBlank
    @Size(max = 100)
    private String addressInfor;
    private String userId;
    private IsActived isActived;

    @Size(max = 10)
    @Pattern(regexp = "^[0-9+\\-()\\s]*$", message = "Invalid phone format")
    private String phoneNumber;

    @Size(max = 50)
    private String fullName;

    @Size(max = 10)
    private String type;

    private boolean isDefault;
}
