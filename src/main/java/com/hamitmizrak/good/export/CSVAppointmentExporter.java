package com.hamitmizrak.good.export;

/*
 CLEAN-UP ÖZETİ
 ✅ Strategy uygulaması: CSV Export/Import.
 ✅ NIO Files API kullanımı, UTF-8, try-with-resources.
 ✅ Basit CSV ayrıştırıcı (tırnak kaçışlarını ele alan) – dependency yok.
 ✅ Text Blocks ile daha okunaklı çıktılar üretilebilir (not: burada satır satır yazdık).
 ✅ Effective Java: kaynakları kapatma, yan etkiyi minimize, Null-safety (boş notlar).
*/

import com.hamitmizrak.good.domain.Appointment;
import com.hamitmizrak.good.domain.AppointmentStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVAppointmentExporter implements ExportStrategy {

    @Override
    public Path exportAppointments(List<Appointment> list, Path target) throws IOException {
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }
        try (BufferedWriter w = Files.newBufferedWriter(
                target, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            w.write("id,patientId,doctorId,dateTime,status,note,version");
            w.newLine();

            for (Appointment a : list) {
                String note = a.getNote() == null ? "" : a.getNote().replace("\"", "\"\"");
                // CSV virgül ayırıcı; not tırnak içine alınır
                String line = String.format("%d,%d,%d,%s,%s,\"%s\",%d",
                        a.getId(), a.getPatientId(), a.getDoctorId(),
                        a.getDateTime(), a.getStatus().name(), note,
                        a.getVersion()
                );
                w.write(line);
                w.newLine();
            }
        }
        return target;
    }

    @Override
    public List<Appointment> importAppointments(Path source) throws IOException {
        List<Appointment> list = new ArrayList<>();
        if (!Files.exists(source)) return list;

        try (BufferedReader r = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            // header
            String header = r.readLine();
            String line;
            while ((line = r.readLine()) != null) {
                // Beklenen: 7 sütun (id,patientId,doctorId,dateTime,status,note,version)
                String[] parts = splitCsv(line, 7);
                if (parts.length < 7) continue;

                Appointment a = Appointment.builder()
                        .patientId(Long.parseLong(parts[1]))
                        .doctorId(Long.parseLong(parts[2]))
                        .dateTime(LocalDateTime.parse(parts[3]))
                        .status(AppointmentStatus.valueOf(parts[4]))
                        .note(unquote(parts[5]))
                        .build();
                a.setId(Long.parseLong(parts[0]));
                a.setVersion(Integer.parseInt(parts[6]));
                // Not: DB’ye upsert etmiyoruz – sadece liste olarak döndürüyoruz.
                list.add(a);
            }
        }
        return list;
    }

    // --- Basit CSV ayrıştırma (tırnak kaçışları ile) ---
    private static String[] splitCsv(String line, int expectedCols) {
        List<String> parts = new ArrayList<>(expectedCols);
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                quoted = !quoted; // tırnak aç/kapat
            } else if (ch == ',' && !quoted) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    private static String unquote(String s) {
        return s.replace("\"\"", "\"");
    }
}
