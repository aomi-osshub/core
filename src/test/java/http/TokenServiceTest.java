package http;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tech.aomi.common.constant.HttpHeader;
import tech.aomi.common.utils.json.Json;
import tech.aomi.filexplorer.form.TokenForm;
import tech.aomi.filexplorer.util.HmacUtil;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author Sean createAt 2021/10/27
 */
public class TokenServiceTest {


    @Test
    public void createToken() throws NoSuchAlgorithmException, InvalidKeyException {

        TokenForm form = new TokenForm();
        form.setTimestamp(new Date());
        form.setRequestId(UUID.randomUUID().toString());

        String data = Json.toJson(form).toString();

        String sign = Base64.getEncoder().encodeToString(HmacUtil.sumHmac("ajngzyi9SjoXoDr452NUe1CzAwelrp5G".getBytes(), data.getBytes(StandardCharsets.UTF_8)));

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeader.SIGNATURE, sign);
        headers.add("X-Access-Key", "617243bd8a8fce59f945803d");
        headers.add("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(data, headers);


        RestTemplate restTemplate = new RestTemplate();

        String res = restTemplate.postForObject("http://localhost:5001/tokens", entity, String.class);

        System.out.println(res);
    }

}
