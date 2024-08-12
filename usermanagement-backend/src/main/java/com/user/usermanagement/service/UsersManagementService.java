package com.user.usermanagement.service;

import com.user.usermanagement.dto.ReqRes;
import com.user.usermanagement.entity.OurUsers;
import com.user.usermanagement.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public ReqRes register(ReqRes registrationRequest){

        ReqRes res = new ReqRes();

        try{

            OurUsers ourUser = new OurUsers();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OurUsers ourUsersResult = usersRepo.save(ourUser);

            if (ourUsersResult.getId()>0) {
                res.setOurUsers((ourUsersResult));
                res.setMessage("User Saved Successfully");
                res.setStatusCode(200);
            }


        }catch (Exception e){
            res.setStatusCode(500);
            res.setError(e.getMessage());
        }
        return res;
    }


    public ReqRes login(ReqRes loginRequest){

        ReqRes res = new ReqRes();

        try{
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));

            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            res.setStatusCode(200);
            res.setToken(jwt);
            res.setRole(user.getRole());
            res.setRefreshToken(refreshToken);
            res.setExpirationTime("24Hrs");
            res.setMessage("Successfully Logged In");

        }catch (Exception e){
            res.setStatusCode(500);
            res.setError(e.getMessage());
        }

        return res;
    }


    public ReqRes refreshToken(ReqRes refreshTokenRequest){

        ReqRes res = new ReqRes();

        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                res.setStatusCode(200);
                res.setToken(jwt);
                res.setRefreshToken(refreshTokenRequest.getToken());
                res.setExpirationTime("24Hr");
                res.setMessage("Successfully Refreshed Token");
            }

            res.setStatusCode(200);
            return res;

        }catch (Exception e){
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
            return res;
        }
    }

    public ReqRes getAllUsers() {

        ReqRes res = new ReqRes();

        try {

            List<OurUsers> result = usersRepo.findAll();

            if (!result.isEmpty()) {
                res.setOurUsersList(result);
                res.setStatusCode(200);
                res.setMessage("Successful");
            } else {
                res.setStatusCode(404);
                res.setMessage("No users found");
            }
            return res;

        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error occurred: " + e.getMessage());
            return res;
        }
    }

    public ReqRes getUsersById(Integer id) {

        ReqRes res = new ReqRes();

        try {

            OurUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            res.setOurUsers(usersById);
            res.setStatusCode(200);
            res.setMessage("Users with id '" + id + "' found successfully");

        } catch (Exception e) {

            res.setStatusCode(500);
            res.setMessage("Error occurred: " + e.getMessage());

        }
        return res;
    }


    public ReqRes deleteUser(Integer userId) {

        ReqRes res = new ReqRes();

        try {

            Optional<OurUsers> userOptional = usersRepo.findById(userId);

            if (userOptional.isPresent()) {

                usersRepo.deleteById(userId);
                res.setStatusCode(200);
                res.setMessage("User deleted successfully");

            } else {
                res.setStatusCode(404);
                res.setMessage("User not found for deletion");
            }

        } catch (Exception e) {

            res.setStatusCode(500);
            res.setMessage("Error occurred while deleting user: " + e.getMessage());

        }
        return res;
    }


    public ReqRes updateUser(Integer userId, OurUsers updatedUser) {
        ReqRes res = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepo.save(existingUser);
                res.setOurUsers(savedUser);
                res.setStatusCode(200);
                res.setMessage("User updated successfully");
            } else {
                res.setStatusCode(404);
                res.setMessage("User not found for update");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return res;
    }

    public ReqRes getMyInfo(String email){
        ReqRes res = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                res.setOurUsers(userOptional.get());
                res.setStatusCode(200);
                res.setMessage("successful");
            } else {
                res.setStatusCode(404);
                res.setMessage("User not found for update");
            }

        }catch (Exception e){
            res.setStatusCode(500);
            res.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return res;

    }


}
