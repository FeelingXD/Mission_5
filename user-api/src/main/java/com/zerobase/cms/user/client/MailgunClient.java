package com.zerobase.cms.user.client;

import com.zerobase.cms.user.client.mailgun.SendMailForm;
import feign.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="mailgun" ,url="https://api.mailgun.net/v3/")
@Qualifier
public interface MailgunClient {

    @PostMapping("sandboxf70622b09aa64dd7ac532062cda62901.mailgun.org/messages")
    Response sendEMail(@SpringQueryMap SendMailForm form);
}
