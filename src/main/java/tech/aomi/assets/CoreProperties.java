package tech.aomi.assets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Sean Create At 2020/4/25
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "core")
public class CoreProperties {

}
