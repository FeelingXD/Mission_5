package com.zerobase.cms.user.client;

import com.zerobase.cms.user.client.mailgun.SendMailForm;
import feign.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="mailgun" ,url="https://api.mailgun.net/v3/")
@Qualifier
public interface MailgunClient {

    @PostMapping("${mailgun.api.domain}"+"/messages")
    Response sendEMail(@SpringQueryMap SendMailForm form);
}
