package com.zerobase.cms.user.service;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDate;


@SpringBootTest
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService service;
//    @Test
//    void signUp(){
//        //given
//        SignUpForm form = SignUpForm.builder()
//                .name("name")
//                .birth(LocalDate.now())
//                .email("test@name")
//                .password("1")
//                .phone("01000000")
//                .build();
//        //when
//        Customer c= service.signUp(form);
//        Assert.isTrue(service.signUp(form).getId()!=null);
//        Assert.notNull(c.getId());
//        Assert.notNull(c.getCreatedAt());
//        //then
//    }
}