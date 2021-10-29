package tech.aomi.osshub.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.aomi.common.exception.ErrorCode;
import tech.aomi.common.web.controller.EnableSignature;
import tech.aomi.common.web.controller.Result;
import tech.aomi.osshub.api.TokenService;
import tech.aomi.osshub.constant.HttpHeader;
import tech.aomi.osshub.entity.Token;
import tech.aomi.osshub.form.TokenForm;

import javax.validation.Valid;
import java.util.Date;

/**
 * token 服务
 *
 * @author Sean createAt 2021/10/22
 */
@Slf4j
@RestController
@RequestMapping("/tokens")
public class TokenController extends AbstractController {

    @Autowired
    private TokenService tokenService;

    @PostMapping
    @EnableSignature
    public Result create(@RequestBody @Valid TokenForm form,
                         @RequestHeader(HttpHeader.ACCESS_KEY) String accessKey) {
        LOGGER.debug("创建Token: {}", form);

        if (System.currentTimeMillis() - (60 * 1000) > form.getTimestamp().getTime()) {
            LOGGER.error("客户端请求时间与服务端时间相差60S以上");
            return Result.create(ErrorCode.PARAMS_ERROR.getCode(), "客户端请求时间与服务端时间相差60S以上");
        }

        Token token = new Token();
        token.setClientId(accessKey);
        token.setRequestId(form.getRequestId());
        token.setExpirationAt(DateUtils.addSeconds(new Date(), form.getExpireAfter()));

        token = tokenService.create(token);

        // 响应结果中隐藏client
        token.setClient(null);

        return success(token);
    }


}
