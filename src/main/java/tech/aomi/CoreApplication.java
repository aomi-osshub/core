package tech.aomi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tech.aomi.assets.CoreProperties;

/**
 * @author Sean Create At 2020/4/25
 */
@EnableConfigurationProperties({CoreProperties.class})
@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
