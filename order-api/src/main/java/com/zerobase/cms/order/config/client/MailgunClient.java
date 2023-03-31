package com.zerobase.cms.order.config.client;

import com.zerobase.cms.user.config.client.mailgun.SendMailForm;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name="mailgun" ,url="https://api.mailgun.net/v3/")
public interface MailgunClient {

    @PostMapping("${mailgun.api.domain}"+"/messages")
    Response sendEMail(@SpringQueryMap SendMailForm form);

}
