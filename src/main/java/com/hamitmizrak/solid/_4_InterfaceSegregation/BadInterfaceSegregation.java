package com.hamitmizrak.solid._4_InterfaceSegregation;

// ❌ KÖTÜ: Basit yazıcıya scan/fax zorunlu; boş/exception metotlar doğar.
interface MultiFunc {
    void print(String s);

    void scan();

    void fax(String n);
}

class SimplePrinterBad implements MultiFunc {
    public void print(String str) {
        System.out.println(str);
    }

    public void scan() { /* gereksiz */ }

    public void fax(String str) {
        System.out.println(str);
        /* gereksiz */
    }
}

public class BadInterfaceSegregation {
    public static void main(String[] args) {
        new SimplePrinterBad().print("Hello"); // çalışır ama tasarım kötü
        // new SimplePrinterBad().scan(); // çalışır ama tasarım kötü
        // new SimplePrinterBad().fax("hello2"); // çalışır ama tasarım kötü
    }
}
