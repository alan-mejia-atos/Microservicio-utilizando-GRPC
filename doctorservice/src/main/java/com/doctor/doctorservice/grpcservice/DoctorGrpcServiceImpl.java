package com.doctor.doctorservice.grpcservice;

import com.doctor.doctorservice.dtos.SimpleDoctorDTO;
import com.doctor.doctorservice.exception.ResourceNotFoundException;
import com.doctor.doctorservice.serviceImpl.DoctorServiceImpl;
import com.doctorsrvc.grpc.*;
import com.doctorsrvc.grpc.Empty;
import com.google.protobuf.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@GrpcService
public class DoctorGrpcServiceImpl extends doctorServiceGrpc.doctorServiceImplBase {
    @Autowired
    private DoctorServiceImpl doctorService;

    @Override
    public void getDoctorById(doctorId request, StreamObserver<Doctor> responseObserver) {
        CompletableFuture<SimpleDoctorDTO> foundDoctor = doctorService.getOneDoctorDTO(request.getId());
        foundDoctor.join();
        Doctor doctorGrpc = Doctor.newBuilder()
                .setId(foundDoctor.join().getId())
                .setUserdId(foundDoctor.join().getUserdId())
                .setFee(foundDoctor.join().getFee())
                .setSpecialtyId(foundDoctor.join().getSpecialtyId())
                .setShiftId(foundDoctor.join().getShiftId())
                .setStatus(foundDoctor.join().getStatus())
                .build();
        responseObserver.onNext(doctorGrpc);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllDoctors(Empty request, StreamObserver<DoctorsList> responseObserver) {
        CompletableFuture<List<SimpleDoctorDTO>> listOfAllDoctors = doctorService.getAllDTO();
        List<Doctor> doctorsGrpcList = new ArrayList<>();

        for (int i= 0; i < listOfAllDoctors.join().size(); i++){
            Doctor doctor = Doctor.newBuilder()
                    .setId(listOfAllDoctors.join().get(i).getId())
                    .setUserdId(listOfAllDoctors.join().get(i).getUserdId())
                    .setFee(listOfAllDoctors.join().get(i).getFee())
                    .setSpecialtyId(listOfAllDoctors.join().get(i).getSpecialtyId())
                    .setShiftId(listOfAllDoctors.join().get(i).getShiftId())
                    .setStatus(listOfAllDoctors.join().get(i).getStatus())
                    .build();
            doctorsGrpcList.add(doctor);
        }

        DoctorsList.Builder listToReturn = DoctorsList.newBuilder();

        for(Doctor oneDoctor: doctorsGrpcList){
            listToReturn.addDoctorsList(oneDoctor);
//           .setDoctorsList(0,doctor);   Para actualizar un indice del arreglo de Objetos
        }

        /* El bloque de abajo hace lo mismo que el bloque de arriba, solo esta como ejemplo
        para ver como se trabaja con el metodo sobrecargado assDoctorsList()  */

//
//        for (int i=0;i<doctorsGrpcList.size(); i++){
//
//            dlist.addDoctorsList(i, Doctor.newBuilder()
//                    .setId(doctorsGrpcList.get(i).getId())
//                    .setUserdId(doctorsGrpcList.get(i).getUserdId())
//                    .setFee(doctorsGrpcList.get(i).getFee())
//                    .setSpecialtyId(doctorsGrpcList.get(i).getSpecialtyId())
//                    .setShiftId(doctorsGrpcList.get(i).getShiftId())
//                    .setStatus(doctorsGrpcList.get(i).getStatus())
//                    .build());
//        }



        responseObserver.onNext(listToReturn.build());
        responseObserver.onCompleted();
    }


    @Override
    public void updateDoctorById(Doctor request, StreamObserver<Doctor> responseObserver) {
        CompletableFuture<SimpleDoctorDTO> existingDoctor = doctorService.getOneDoctorDTO(request.getId());
        existingDoctor.join();
        if(existingDoctor == null){
            responseObserver.onError(new ResourceNotFoundException("Doctor","Id",request.getId()));
        }

        existingDoctor.join().setFee(request.getFee());
        existingDoctor.join().setUserdId(request.getUserdId());
        existingDoctor.join().setStatus(request.getStatus());
        existingDoctor.join().setShiftId(request.getShiftId());
        existingDoctor.join().setSpecialtyId(request.getSpecialtyId());

        ModelMapper modelMapper = new ModelMapper();
        com.doctor.doctorservice.models.Doctor model = modelMapper.map(existingDoctor, com.doctor.doctorservice.models.Doctor.class);
        model.setModificationTime(new Date());
        model.setFee(request.getFee());
        model.setShiftId(request.getShiftId());
        model.setSpecialtyId(request.getSpecialtyId());
        model.setStatus(request.getStatus());
        model.setUserdId(request.getUserdId());

        doctorService.updateDoctor(model,request.getId());


        Doctor doctorToUpdate = Doctor.newBuilder()
                .setId(request.getId())
                .setUserdId(request.getUserdId())
                .setFee(request.getFee())
                .setSpecialtyId(request.getSpecialtyId())
                .setShiftId(request.getShiftId())
                .setStatus(request.getStatus())
                .build();

        responseObserver.onNext(doctorToUpdate);
        responseObserver.onCompleted();

    }
}