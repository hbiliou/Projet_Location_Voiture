package com.locationvoiture.controller;

import com.locationvoiture.dto.AuthenticationRequest;
import com.locationvoiture.dto.AuthenticationResponse;
import com.locationvoiture.dto.SignupRequest;
import com.locationvoiture.dto.UserDto;
import com.locationvoiture.entity.User;
import com.locationvoiture.repsitory.UserRepository;
import com.locationvoiture.service.AuthService;
import com.locationvoiture.services.jwt.UserService;
import com.locationvoiture.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
   @PostMapping("/signup")
   public ResponseEntity<?> signupCustomer(@RequestBody SignupRequest signupRequest){
       if(authService.hasCustomerWithEmail(signupRequest.getEmail()))
           return new ResponseEntity<>("Email deja existant",HttpStatus.NOT_ACCEPTABLE);
        UserDto createdCustomerDto =authService.createCustomer(signupRequest);
        if(createdCustomerDto == null)return new ResponseEntity<>
                ("le client n'est pas créer ", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(createdCustomerDto,HttpStatus.CREATED);

    }
    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)throws
            BadCredentialsException,
            DisabledException,
            UsernameNotFoundException{
       try {
       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
               authenticationRequest.getEmail(),authenticationRequest.getPassword()));

       }catch (BadCredentialsException e){
           throw new BadCredentialsException("username ou pasword inccorecte");
       }
       final UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser=userRepository.findFirstByEmail(userDetails.getUsername());
        final String jwt =jwtUtil.generateToken(userDetails);
        AuthenticationResponse authenticationResponse =new  AuthenticationResponse();
        if(optionalUser.isPresent()){
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserId(optionalUser.get().getId());
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
        }
        return authenticationResponse;
    }



}
