package me.smmukesh.ecommerceproject.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3,max = 20)
    private String username;

    @NotBlank
    @Email
    @Size(max = 40)
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 30)
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{6,30}$",
            message = "Password must be 6-30 characters long, include at least one uppercase letter and one special symbol."
    )
    private String password;
}
