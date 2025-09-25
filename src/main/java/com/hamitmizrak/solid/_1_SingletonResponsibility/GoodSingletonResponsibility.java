package com.hamitmizrak.solid._1_SingletonResponsibility;

/*public void process(double price, double tax){
    // 1- Dikkat Hesaplama iş kuramı
    double total = price+price*tax;

    // 2- Konsolea yadırmak
    System.out.println("Total: "+total);

    // 3- Kalıcık: Veri tabanı
    // savetoDb(total);
    System.out.println("Database saved: "+total);

}*/

// LOMBOK


// 1- Sadece Hesaplama yapmak
class Calculator{
    // Toplama hesabı
    double total(double total, double tax){
        return total+total*tax;
    }
}

// 2- Ekrana çıktı almak
class PrinterData{
    // Toplama hesabı
   void printData(double total){
       System.out.println("Total: "+total);
       // log.info("")
   }
}


// 3- Ekrana çıktı almak
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
        double totalData=  calculator.total(price,tax);
        printer.printData(totalData);
        databasePersist.saveToDb(totalData);
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
