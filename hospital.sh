#! /bin/bash
#!/bin/bash
# bash (tercihiniz olsun) ancak bh eskidi

# Eğer database_memory_persist varsa sil
echo "Backend (Spring Boot) - Frontend(React JS) Starting Dockerize"

#########################################################
# User Variable
INSTALL="Yükleme"


#########################################################
# Maven deployment
chmod +x shell_countdown.sh


#########################################################
# Version info
version_info(){
  ./shell_countdown.sh
  mvn -v
  git -v
  java -version
  javac -version
  docker version
}
version_info

#########################################################
# Spring run
# Database File Delete
spring_boot_run() {

    # Geri Sayım
    ./shell_countdown.sh

    echo -e "\n###### ${INSTALL} ######  "
    read -p "Hospital Projesini spring üzerinden çalıştırmak istiyor musunuz ? e/h " springRun
    if [[ $springRun == "e" || $springRun == "E" ]]; then
        echo -e "Spring Boot Run ..."
        # Geri Sayım
        echo -e "Target siliniyor ..."
        ./shell_countdown.sh

         mvn -U -DskipTests clean package
         # rm -rf target
         ./shell_countdown.sh
         #mvn -U  clean package

         echo -e "Spring boot başlıyor ..."
         mvn spring-boot:run
         #mvn -Dspring-boot.run.main-class=com.hamitmizrak.good.menu.MainConsole spring-boot:run

         #mvn clean package
         #rm -rf database_memory_persist
         #echo -e "Bulunduğum dizin => $(pwd)\n"
    else
        echo -e "Spring boot başlamadı...."
    fi
}
spring_boot_run

#########################################################
#########################################################
#########################################################
#########################################################
#########################################################
