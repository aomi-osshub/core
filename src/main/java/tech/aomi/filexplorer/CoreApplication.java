package tech.aomi.filexplorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tech.aomi.filexplorer.CoreProperties;

/**
 * File Explorer
 * 文件资源管理器
 *
 * @author Sean Create At 2020/4/25
 */
@EnableConfigurationProperties({CoreProperties.class})
@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
