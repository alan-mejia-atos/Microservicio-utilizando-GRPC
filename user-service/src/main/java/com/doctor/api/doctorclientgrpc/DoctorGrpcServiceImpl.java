package com.doctor.api.doctorclientgrpc;

import com.doctor.api.gepcmodel.DoctorGrpc;
import com.doctor.api.serviceImpl.UserServiceImpl;
import com.doctorsrvc.grpc.Doctor;
import com.doctorsrvc.grpc.doctorId;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorGrpcServiceImpl {
    @Autowired
    private UserServiceImpl userService;


//    private doctorServiceGrpc.doctorServiceFutureStub doctorServiceFutureStub; //For asynchronous call
//
//    // For synchronous call
@GrpcClient("doctor-service")
private com.doctorsrvc.grpc.doctorServiceGrpc.doctorServiceBlockingStub doctorServiceBlockingStub;

    public DoctorGrpc grpcGetByIdRRequest(Long id){
        System.out.println("Empieza llamada a base de datos");
        try {
            final Doctor doctor= doctorServiceBlockingStub.getDoctorById(doctorId.newBuilder().setId(id).build());
            System.out.println("Valor obetnido" + doctor.getId());

            System.out.println("Bien 1");
            DoctorGrpc doctorGrpc = new DoctorGrpc();
            System.out.println("Bien 2");
            doctorGrpc.setId(doctor.getId());
            System.out.println("Bien 3");
            doctorGrpc.setFee(doctor.getFee());
            System.out.println("Bien 4");
            doctorGrpc.setUserdId(doctor.getUserdId());
            System.out.println("Bien 6");
            doctorGrpc.setShiftId(doctor.getShiftId());
            System.out.println("Bien 5");
            doctorGrpc.setStatus(doctor.getStatus());
            System.out.println("Bien 7");
            doctorGrpc.setSpecialtyId(doctor.getSpecialtyId());
            System.out.println("Aqui retorna el objeto");
            return doctorGrpc;
        }catch (final Exception e){
            DoctorGrpc doctorGrpc = null;
            return doctorGrpc;
        }
    }
}
