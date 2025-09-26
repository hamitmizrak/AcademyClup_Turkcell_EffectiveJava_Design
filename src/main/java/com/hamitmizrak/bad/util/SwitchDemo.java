package com.hamitmizrak.bad.util;


import com.hamitmizrak.bad.model.Appointment;

public class SwitchDemo {
    public static void printAny(Object o) {
        // Kötü: null kontrolü yok
        switch (o) {
            case String s -> System.out.println("String:" + s.toUpperCase());
            case Integer x -> System.out.println("Integer:" + (x + 1));
            case Appointment a -> System.out.println("Appt:" + a.id + "@" + a.start);
            default -> System.out.println("Bilmiyorum: " + o);
        }
    }
}
