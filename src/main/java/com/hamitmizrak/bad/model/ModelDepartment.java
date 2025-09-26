package com.hamitmizrak.bad.model;

// KÖTÜ: (Enum doğru ama yanlış kullanım için zemin)
public enum ModelDepartment {
    CARDIOLOGY, PEDIATRICS, DERMATOLOGY, NEUROLOGY, ORTHOPEDICS   // ❌ KÖTÜ: Tür azlığı
}
