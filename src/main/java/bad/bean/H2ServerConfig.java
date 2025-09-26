package bad.bean;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class H2ServerConfig {

    // H2 Web Console (http://localhost:4444) - tek örnek
    /*@Bean(destroyMethod = "stop")
    public Server h2WebConsoleServer() throws SQLException {
        // port çakışmalarını önlemek için sabit ve boş bir port ver
        return Server.createWebServer("-web", "-webPort", "4444", "-ifExists").start();
    }*/

    // (Opsiyonel) TCP server: harici araçlar veya IDE bağlansın
    /*@Bean(destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
    }*/
}
