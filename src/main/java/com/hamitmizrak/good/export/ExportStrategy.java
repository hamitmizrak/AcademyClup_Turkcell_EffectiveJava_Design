package com.hamitmizrak.good.export;

/*
 CLEAN-UP ÖZETİ
 ✅ Strategy: CSV (ve ileride JSON/Parquet vs.) aynı arayüzle yönetilir.
 ✅ Tek sorumluluk: Export/Import kontratı.
*/

/*
 NEDEN FACTORY METHOD?
 - "Hangi formata göre hangi exporter?" kararını tek yerde toparlar (tek sorumluluk).
 - Yeni format eklemek: Yeni sınıf (Strategy implementasyonu) + factory’e tek satır.
 - Çağıran kodlar (Console/Service) somut sınıfı bilmez -> gevşek bağlılık (loose coupling).
 - Mevcut CSV yolu bozulmaz; default olarak .csv yine CSV exporter verir.
*/

import com.hamitmizrak.good.domain.Appointment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ExportStrategy {
    Path exportAppointments(List<Appointment> list, Path target) throws IOException;
    List<Appointment> importAppointments(Path source) throws IOException;
}
