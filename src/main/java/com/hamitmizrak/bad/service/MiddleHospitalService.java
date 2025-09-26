package com.hamitmizrak.bad.service;


import com.hamitmizrak.bad.model.ModelAppointment;
import com.hamitmizrak.bad.model.ModelAppointmentStatus;
import com.hamitmizrak.bad.repository.DataAppointmentRepository;


import java.util.*;
/*
❌ KÖTÜ Ne yapıyor? Konsoldan randevu ekleme/listeleme/güncelleme/silme.
Neden kötü?
Service iş kuralları yok; CLI içinde iş kuralı.
Optional yanlış (get() patlatır), Regex kırılgan, Reflection gereksiz,
Thread’ler yanlış (race condition), System.out ile sözde logging, i18n yok.
*/
public class MiddleHospitalService {

    // Kötü: state global gibi kullanılıyor
    public static List<ModelAppointment> CACHE = new ArrayList();

    public static Long createAppointment(Long patientId, Long doctorId, Date start, int minutes) {
        ModelAppointment a = new ModelAppointment(null, patientId, doctorId, start, minutes);
        Long id = DataAppointmentRepository.save(a);
        a.id = id; // Kötü: id dışarıdan set
        CACHE.add(a); // Kötü: senkronizasyon yok
        return id;
    }

    public static List<ModelAppointment> list() {
        // Kötü: repo + cache karışık, tutarlılık yok
        List<ModelAppointment> db = DataAppointmentRepository.findAll();
        db.addAll(CACHE);
        // Kötü: çakışmalar kontrol edilmiyor
        return db;
    }

    public static void changeStatus(Long id, String newStatus) {
        // Kötü: enum dönüşüm hataya açık
        ModelAppointmentStatus st = ModelAppointmentStatus.valueOf(newStatus);
        DataAppointmentRepository.updateStatus(id, st);
        // Kötü: cache güncellemesi yok
    }

    public static void remove(Long id) {
        DataAppointmentRepository.deleteById(id);
        // Kötü: CACHE temizlenmedi, tutarsızlık
    }
}
