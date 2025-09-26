package com.hamitmizrak.bad.service;


import com.hamitmizrak.bad.model.Appointment;
import com.hamitmizrak.bad.model.AppointmentStatus;
import com.hamitmizrak.bad.repository.AppointmentRepository;


import java.util.*;
/*
❌ KÖTÜ Ne yapıyor? Konsoldan randevu ekleme/listeleme/güncelleme/silme.
Neden kötü?
Service iş kuralları yok; CLI içinde iş kuralı.
Optional yanlış (get() patlatır), Regex kırılgan, Reflection gereksiz,
Thread’ler yanlış (race condition), System.out ile sözde logging, i18n yok.
*/
public class HospitalService {

    // Kötü: state global gibi kullanılıyor
    public static List<Appointment> CACHE = new ArrayList();

    public static Long createAppointment(Long patientId, Long doctorId, Date start, int minutes) {
        Appointment a = new Appointment(null, patientId, doctorId, start, minutes);
        Long id = AppointmentRepository.save(a);
        a.id = id; // Kötü: id dışarıdan set
        CACHE.add(a); // Kötü: senkronizasyon yok
        return id;
    }

    public static List<Appointment> list() {
        // Kötü: repo + cache karışık, tutarlılık yok
        List<Appointment> db = AppointmentRepository.findAll();
        db.addAll(CACHE);
        // Kötü: çakışmalar kontrol edilmiyor
        return db;
    }

    public static void changeStatus(Long id, String newStatus) {
        // Kötü: enum dönüşüm hataya açık
        AppointmentStatus st = AppointmentStatus.valueOf(newStatus);
        AppointmentRepository.updateStatus(id, st);
        // Kötü: cache güncellemesi yok
    }

    public static void remove(Long id) {
        AppointmentRepository.deleteById(id);
        // Kötü: CACHE temizlenmedi, tutarsızlık
    }
}
