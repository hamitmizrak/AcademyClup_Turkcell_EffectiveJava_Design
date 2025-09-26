package com.hamitmizrak;

import com.hamitmizrak.bad.BadHospitalAppMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainProject {

    // PSVM
    public static void main(String[] args) {
        SpringApplication.run(MainProject.class, args);

        // Bad
        BadHospitalAppMain badHospitalAppMain= new BadHospitalAppMain();
        badHospitalAppMain.allData();
    }
}
