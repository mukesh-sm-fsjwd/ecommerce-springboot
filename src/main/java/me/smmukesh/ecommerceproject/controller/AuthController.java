package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.model.AppRole;
import me.smmukesh.ecommerceproject.model.Role;
import me.smmukesh.ecommerceproject.model.User;
import me.smmukesh.ecommerceproject.repository.RoleRepository;
import me.smmukesh.ecommerceproject.repository.UserRepository;
import me.smmukesh.ecommerceproject.security.jwt.JwtUtils;
import me.smmukesh.ecommerceproject.security.request.LoginRequest;
import me.smmukesh.ecommerceproject.security.request.SignupRequest;
import me.smmukesh.ecommerceproject.security.response.MessageResponse;
import me.smmukesh.ecommerceproject.security.response.UserInfoResponse;
import me.smmukesh.ecommerceproject.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Autowired
    public AuthController(JwtUtils jwtUtils,
                          AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            Map<String,Object> map = new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(map);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        UserInfoResponse loginResponse = new UserInfoResponse();
        loginResponse.setId(userDetails.getId());
        loginResponse.setUsername(userDetails.getUsername());
        loginResponse.setRoles(roles);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error : Username Already Taken !"));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error : Email Already Taken !"));
        }

        User user = new User();
        user.setUserName(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error : Role not found !"));
            roles.add(userRole);
        }else {
            strRoles.forEach(role -> {
                switch (role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error : Role not found !"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error : Role not found !"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error : Role not found !"));
                        roles.add(userRole);
                        break;
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new MessageResponse("User Registered Successfully"));
    }

    /*
        Used to show the name of the user on the home page
        when the user is logged in to our application.
     */
    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        return authentication != null ? authentication.getName() : "";
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        UserInfoResponse loginResponse = new UserInfoResponse();
        loginResponse.setId(userDetails.getId());
        loginResponse.setUsername(userDetails.getUsername());
        loginResponse.setRoles(roles);
        return ResponseEntity.status(HttpStatus.OK)
                .body(loginResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie responseCookie = jwtUtils.getCleanCookie();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body(new MessageResponse("You've been sign out"));
    }
}
