package com.example.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.system.common.JwtUtil;
import com.example.system.common.RSAUtils;
import com.example.system.common.Result;
import com.example.system.domain.User;
import com.example.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping()
    public Result register(@RequestBody User user){
        System.out.println(user);
        LambdaQueryWrapper<User> queryChainWrapper =new LambdaQueryWrapper<>();
        queryChainWrapper.eq(User::getUserName,user.getUserName());
        User one = userService.getOne(queryChainWrapper);
        if(one == null){
            userService.save(user);
            return Result.success(user);
        }else{
            return Result.error("This account already exists");
        }
    }


    @PostMapping("/login")
    public Result<User> login(@RequestBody  User user, HttpServletRequest request) throws Exception {

        String password = user.getPassword();

        //Decrypt username and password with private key.
        password = RSAUtils.decryptWithPrivate(password);
        String userName = RSAUtils.decryptWithPrivate(user.getUserName());

        // Query the database based on the password.
        LambdaQueryWrapper<User> queryChainWrapper =new LambdaQueryWrapper<>();
        queryChainWrapper.eq(User::getUserName,userName);
        User one = userService.getOne(queryChainWrapper);

        // If the username is not found in the database, return a login failure result.
        if (one==null){
            return Result.error("Login fail");
        }
        // Compare passwords, if they don't match, return a login failure result.
        if(!one.getPassword().equals(password)){
            return Result.error("Login fail");
        }
        // Use JWT Generate Token
        String token = JwtUtil.createToken(user);
        System.out.println(token);
        request.getSession().setAttribute("token",token);

        return Result.success(one).add("token",token);
    }


    // Generate Public Key
    // Get Public Key through a GET request.
    @GetMapping("/getPublicKey")
    public Result<String>  getPublicKey(){
        String publicKey =RSAUtils.getPublicKey();
        return Result.success(publicKey);
    }
}
