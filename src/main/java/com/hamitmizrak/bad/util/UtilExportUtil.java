package com.hamitmizrak.bad.util;


import com.hamitmizrak.bad.model.ModelAppointment;
import com.hamitmizrak.bad.service.MiddleHospitalService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UtilExportUtil {

    public static String path() {
        return Path.of(System.getProperty("user.home"), "hospital-appointments.bin").toString();
    }

    // Kötü: try-with-resources yok, hata yönetimi yok
    public static void exportAppointments() {
        List<ModelAppointment> list = MiddleHospitalService.list();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path()));
            oos.writeObject(new ArrayList<>(list));
            oos.flush();
            oos.close();
            System.out.println("Export tamam: " + path());
        } catch (Exception e) {
            System.out.println("Export hata (yok sayıldı).");
        }
    }

    @SuppressWarnings("unchecked")
    public static void importAppointments() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path()));
            List<ModelAppointment> list = (List<ModelAppointment>) ois.readObject();
            ois.close();
            // Kötü: DB yerine bellek CACHE'e ekleyelim → tutarsızlık
            MiddleHospitalService.CACHE.addAll(list);
            System.out.println("Import edildi, sayi=" + list.size());
        } catch (Exception e) {
            System.out.println("Import hata (yok sayıldı).");
        }
    }
}
