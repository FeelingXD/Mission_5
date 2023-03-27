package com.zerobase.cms.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import user.service.EmailSendService;

@SpringBootTest
class EmailSendServiceTest {

    @Autowired
    private EmailSendService emailSendService;
    @Test
    void testSendmail(){
        //given
        var e =emailSendService.sendEmail();
        //when

        //then
    }

}