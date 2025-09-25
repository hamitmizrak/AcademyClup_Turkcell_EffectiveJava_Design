package com.hamitmizrak;


import com.hamitmizrak.bad.HospitalAppMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainProject {

    public static void main(String[] args) {
        SpringApplication.run(MainProject.class, args);

        // Bad
        HospitalAppMain hospitalAppMain = new HospitalAppMain();
        hospitalAppMain.allData();

        // Good

    }
}
