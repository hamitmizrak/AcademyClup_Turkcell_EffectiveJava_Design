package com.hamitmizrak;

import com.hamitmizrak.bad.BadHospitalAppMain;
import com.hamitmizrak.good.menu.MainConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainProject {

    // PSVM
    public static void main(String[] args) throws Exception {
        // Bad
        //BadHospitalAppMain badHospitalAppMain= new BadHospitalAppMain();
        //badHospitalAppMain.allData();

        // Good
        //MainConsole MainConsole= new MainConsole();
        //MainConsole.run();

        SpringApplication.run(MainProject.class, args);
    }
}
