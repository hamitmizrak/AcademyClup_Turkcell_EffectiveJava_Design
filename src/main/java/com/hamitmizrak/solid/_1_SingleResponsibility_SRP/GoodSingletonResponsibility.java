package com.hamitmizrak.solid._1_SingleResponsibility_SRP;

// ✅ İYİ: Sorumluluklar ayrıldı; değişimler lokal, test daha kolay.
// Ne? Bir sınıfın tek bir değişim nedeni olmalı (tek iş yap, onu iyi yap).
//Neden? Değişim etkisi küçülür, test kolaylaşır, yeniden kullanım artar.
//Ne zaman? Bir sınıf birden çok bağlamda değişiyorsa (hesaplama + IO + UI), SRP’yi uygula.
//Koku: “God class”, dev metotlar, birbirinden kopuk işler aynı sınıfta.

// Özet: Bir sınıfın tek bir değişim nedeni olmalı; hesap, çıktı, kalıcılık birbirinden ayrılmalı.

// 1- Sadece Hesaplama yapmak
// sadece hesap
class Calculator{
    // Toplama hesabı
    double total(double total, double tax){
        return total+total*tax;
    }
}

// 2- Ekrana çıktı almak
// sadece IO
class PrinterData{
    // Toplama hesabı
   void printData(double total){
       System.out.println("Total: "+total);
       // log.info("")
   }
}


// 3- Ekrana çıktı almak
// sadece kalıcılık
class DatabasePersist{
    // Toplama hesabı
    void saveToDb(double total){
        System.out.println("Saved: "+total);
        // log.info("")
    }
}

class AllMain{
    // Instance
    Calculator calculator = new Calculator();
    PrinterData printer = new PrinterData();
    DatabasePersist  databasePersist = new DatabasePersist();

    void allProcess(double price, double tax){
        double totalData=  calculator.total(price,tax);// iş kuralı izole
        printer.printData(totalData);  // IO ayrı
        databasePersist.saveToDb(totalData); // kalıcılık ayrı
    }
}


// Main Class
public class GoodSingletonResponsibility {

    public static void main(String[] args) {
        // Instance
        AllMain allMain= new AllMain();
        allMain.allProcess(12,20);

    }
}


// Evet:  Direk hastane randevü geçmek ve SOLID ekran üzerinden analtımını sağlamak
// Hayır