package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.exception.CustomerException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class SignUpApplication {


    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email,String code){
        signUpCustomerService.verifyEmail(email,code);
    }

    public String customerSignUp(SignUpForm form){
        if(signUpCustomerService.isEmailExist(form.getEmail())){
            throw new CustomerException(ErrorCode.ALREADY_REGISTERED_USER);
            //exception
        }else{
            Customer c= signUpCustomerService.signUp(form);
            LocalDateTime now =LocalDateTime.now();

            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("signUp@no-reply.com")
                    .to(form.getEmail())
                    .subject("verification email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), code))
                    .build();

            mailgunClient.sendEMail(sendMailForm);
            signUpCustomerService.changeCustomerValidationEmail(c.getId(),code);
            return "회원 가입에 성공하였습니다.";
        }
    }


    private String getRandomCode(){
        return RandomStringUtils.random(10,true,true);
    }

    private String getVerificationEmailBody(String email, String name, String code){
        StringBuilder builder= new StringBuilder();
        return builder.append("Hello ").append(name).append("! PLEASE CLICK LINK FOR VERIFICATION \n\n")
                .append("http://localhost:8081/signup/verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}