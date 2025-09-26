package com.hamitmizrak.solid._4_InterfaceSegregation;


// ✅ İYİ: İhtiyaç kadar sözleşme; gereksiz bağımlılık yok.
interface Printer {
    void print(String str);
}

interface ScannerDev {
    void scan();
}

interface Fax {
    void faxData(String str);
}


class SimplePrinter implements Printer {
    public void print(String str) {
        System.out.println(str);
    }
}

// Ne? İstemci kullanmadığı metotlara bağımlı kalmasın; arayüzleri küçük tut.
// Neden? Gereksiz implementasyon ve sahte (no-op) metotlar kaybolur; test sadeleşir.
// Ne zaman? Bir arayüzü implement ederken iki metodu boş bırakıyorsan böl.
// Koku: “Hepsi-bir-arada” arayüz, UnsupportedOperationException/boş gövde.

// Main Class
public class GoodInterfaceSegregation {
    public static void main(String[] args) {
        Printer printer = new SimplePrinter();
        printer.print("Hello");
    }
}

