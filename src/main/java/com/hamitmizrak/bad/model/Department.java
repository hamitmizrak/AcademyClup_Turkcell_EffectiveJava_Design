package com.hamitmizrak.bad.model;

// KÖTÜ: (Enum doğru ama yanlış kullanım için zemin)
public enum Department {
    CARDIOLOGY, PEDIATRICS, DERMATOLOGY, NEUROLOGY, ORTHOPEDICS   // ❌ KÖTÜ: Tür azlığı
}
