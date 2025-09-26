package com.hamitmizrak.solid._3_LiskovSubsition;


// ✅ İYİ: Uçma ayrı bir sözleşme; penguen uçma vaadi vermezse ihlal yok.
interface IFlyer {
    void fly();
}

class Eagle implements IFlyer {
    public void fly() {
        System.out.println("eagle fly");
    }
}

class Penguin {
} // uçma sözü yok, dolayısıyla LSP korunur


// Ne? Alt tür, üst türün sözleşmesini bozmadan onun yerine geçebilmelidir.
// Neden? Polimorfizm güvenliği; sürpriz istisna/önkoşul artışı olmaz.
// Ne zaman? “Alt sınıf bazı metotları atıyor / exception fırlatıyor” → alarm.
// Koku: UnsupportedOperationException, önkoşul sıkılaştırma, sözleşmeyi zayıflatma.
public class GoodLiskovSubsition {

    // PSVM
    public static void main(String[] args) {
        IFlyer iFlyer = new Eagle();
        iFlyer.fly(); // güvenli
    }
}
