package com.hamitmizrak.solid._3_LiskovSubsition;

// ❌ KÖTÜ: Bird.fly() "uçmayı" vaat ediyor; Penguin bunu atıyor → LSP ihlali.
class Bird {
    void fly() {
        System.out.println("fly");
    }
}

class PenguinBad extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException();
    }
}

// CLASS
public class BadLiskovSubsition {

    // PSVM
    public static void main(String[] args) {
        Bird b = new PenguinBad();     // statik tür Bird, dinamik PenguinBad
        try {
            b.fly();
        }               // runtime'da patlar
        catch (UnsupportedOperationException e) {
            System.out.println("LSP ihlali!");
        }
    }

}
