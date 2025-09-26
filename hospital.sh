#! /bin/bash
#!/bin/bash
# bash (tercihiniz olsun) ancak bh eskidi

# Eğer database_memory_persist varsa sil
echo "Backend (Spring Boot) - Frontend(React JS) Starting Dockerize"

#########################################################
# User Variable
UPDATED="Güncelleme"
CLEANER="Temizleme"
INSTALL="Yükleme"
DELETED="Silme"
CHMOD="Erişim İzni"
INFORMATION="Genel Bilgiler Ports | NETWORKING"
UFW="Uncomplicated Firewall Ggüvenlik duvarı Yöentim Araçı"
LOGOUT="Sistemi Tekrar Başlatmak"
CHECK="Yüklenecek Paket bağımlılıkları"
PACKAGE="Paket Sistemde Yüklü mü"
JDK="JDK Kurmak"
JENKINS="Jenkins"
TOMCAT="Apache Tomcat"
POSTGRESQL="Postgresql"
SONARQUBE="SonarQube"
DOCKER_PULL="Docker Pulling"
LOGIN="Docker Login"
LOGOUT="Docker Logout"
PORTAINER="Docker Portainer"
DOCKERCOMPOSE="Docker Compose"


#########################################################
# Maven deployment
#chmod +x shell_maven_docker.sh
#chmod +x shell_manuel_project.sh
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
# docker_network
springboot_network(){

}
docker_network

#########################################################
#########################################################
#########################################################
#########################################################
#########################################################
