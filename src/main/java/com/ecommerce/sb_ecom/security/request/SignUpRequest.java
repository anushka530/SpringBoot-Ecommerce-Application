package com.ecommerce.sb_ecom.security.request;

import com.ecommerce.sb_ecom.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
@Data
public class SignUpRequest {
    @NotBlank
    @Size(min=5,max=20)
    private String username;
    @NotBlank
    @Email
    private String email;

    private Set<String> roles;
    @NotBlank
    @Size(min=6,max=20)
    private String password;
}
