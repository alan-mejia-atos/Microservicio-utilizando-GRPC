package com.doctor.api.controller;

import com.doctor.api.doctorclientgrpc.DoctorGrpcServiceImpl;
import com.doctor.api.dtos.CompleteInfoDTO;
import com.doctor.api.dtos.SimpleUserDTO;
import com.doctor.api.gepcmodel.DoctorGrpc;
import com.doctor.api.models.User;
import com.doctor.api.serviceImpl.UserServiceImpl;
import com.doctorsrvc.grpc.Doctor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/users")
public class UserController extends GenericControllerImpl<User,UserServiceImpl> {


    @Autowired
    private UserServiceImpl userService;

    @Override
    @GetMapping("")
    public CompletableFuture<ResponseEntity> getAll(){
        return userService.getAllDTO().thenApply(ResponseEntity::ok);
    }

    @Override
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity> getOne(@PathVariable Long id){
        CompletableFuture<SimpleUserDTO> exists = userService.getOneUserDTO(id);
        return exists.thenApply(ResponseEntity::ok);
    }

    @PostMapping("")
    public CompletableFuture<ResponseEntity> save(@RequestBody User user){
        user.setModificationTime(new Date());
        user.setCreationTime(new Date());
        user.setRole(1);
        user.setStatus(1);
        return userService.save(user).thenApply(ResponseEntity::ok);
    }

    @Override
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity> update(@RequestBody User user,@PathVariable("id") Long id){
        return userService.updateUser(user,id).thenApply(ResponseEntity::ok);
    }

    @CircuitBreaker(name = "doctorsCB", fallbackMethod = "fallbackSaveDoctorProfile")
    @Override
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity> delete(@PathVariable("id")Long id){
        return userService.deleteUser(id).thenApply(ResponseEntity::ok);
    }

    private CompletableFuture<ResponseEntity> fallbackSaveDoctorProfile(@PathVariable("id")Long id, RuntimeException e){
        return CompletableFuture.completedFuture(ResponseEntity.ok("Response Entity"));
    }

    @Autowired
    private DoctorGrpcServiceImpl doctorGrpcService;

    @GetMapping("doctorgrpc/{id}")
    public DoctorGrpc getDoctorFromUsersAPI(@PathVariable Long id){
        System.out.println("Llamada al controlador");
        return doctorGrpcService.grpcGetByIdRRequest(id);
    }

    //  Ambas llamadas deben ser asincronas para que este metodo funcione
//    @GetMapping("completeinfo/{id}")
//    public CompleteInfoDTO getCompletInfo(@PathVariable Long id){
//        return userService.userAndDoctorProfile(id);
//    }

}
