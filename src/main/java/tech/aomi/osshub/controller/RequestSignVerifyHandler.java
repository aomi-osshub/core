package tech.aomi.osshub.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.aomi.common.constant.HttpHeader;
import tech.aomi.common.exception.SignatureException;
import tech.aomi.common.web.controller.AbstractRequestSignVerifyHandler;
import tech.aomi.osshub.entity.Client;
import tech.aomi.osshub.repositry.ClientRepository;
import tech.aomi.osshub.util.HmacUtil;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static tech.aomi.osshub.constant.HttpHeader.ACCESS_KEY;

/**
 * @author Sean createAt 2021/10/27
 */
@Slf4j
@RestControllerAdvice
public class RequestSignVerifyHandler extends AbstractRequestSignVerifyHandler {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    protected void verify(byte[] body) {
        String clientId = request.getHeader(ACCESS_KEY);
        if (StringUtils.isEmpty(clientId)) {
            throw new IllegalArgumentException("无效的AccessKey");
        }
        String reqSignStr = request.getHeader(HttpHeader.SIGNATURE);
        if (StringUtils.isEmpty(reqSignStr)) {
            LOGGER.error("签名信息为空");
            throw new SignatureException("签名信息为空");
        }
        LOGGER.info("签名数据={} 请求签名={}", new String(body, StandardCharsets.UTF_8), reqSignStr);

        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("无效的AccessKey"));

        try {
            byte[] sign = HmacUtil.sumHmac(client.getSecretKey().getBytes(), body);
            String signStr = Base64.getEncoder().encodeToString(sign);
            if (!signStr.equals(reqSignStr)) {
                LOGGER.info("服务端签名={}", signStr);
                throw new SignatureException("签名不正确");
            }
        } catch (Exception e) {
            LOGGER.error("签名校验失败: {}", e.getMessage(), e);
            throw new SignatureException("签名校验失败", e);
        }

    }


}
