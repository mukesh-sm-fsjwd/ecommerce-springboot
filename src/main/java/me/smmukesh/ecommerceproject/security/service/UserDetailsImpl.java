package me.smmukesh.ecommerceproject.security.service;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.smmukesh.ecommerceproject.model.User;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Converts a CustomUser object into a UserDetailsImpl object.
     *
     * @param user the custom user object
     * @return a UserDetailsImpl object
     */
    public static UserDetailsImpl build(User user){

        /**
         * Converts user roles into SimpleGrantedAuthority objects.
         * The role name is obtained from the RoleName enum using name().
         */
        List<SimpleGrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .toList();

        return new UserDetailsImpl(user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                grantedAuthorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
        Compares the UserDetails Object by Ids
    **/
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id,user.id);
    }
}
