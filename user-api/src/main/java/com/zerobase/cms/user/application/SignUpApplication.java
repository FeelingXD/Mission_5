package com.zerobase.cms.user.application;

import com.zerobase.cms.user.config.client.MailgunClient;
import com.zerobase.cms.user.config.client.mailgun.SendMailForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class SignUpApplication {


    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SellerService sellerService;

    public void customerVerify(String email,String code){
        signUpCustomerService.verifyEmail(email,code);
    }

    public void sellerVerify(String email,String code){
        sellerService.verifyEmail(email,code);
    }

    public String sellerSignUp(SignUpForm form){
        if(sellerService.isEmailExist(form.getEmail())){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
            //exception
        }else{
            Seller c= sellerService.singUp(form);
            LocalDateTime now =LocalDateTime.now();

            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("signUp@no-reply.com")
                    .to(form.getEmail())
                    .subject("verification email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(),"seller", code))
                    .build();

            mailgunClient.sendEMail(sendMailForm);
            sellerService.changeSellerValidationEmail(c.getId(),code);
            return "회원 가입에 성공하였습니다.";
        }
    }
    public String customerSignUp(SignUpForm form){
        if(signUpCustomerService.isEmailExist(form.getEmail())){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
            //exception
        }else{
            Customer c= signUpCustomerService.signUp(form);
            LocalDateTime now =LocalDateTime.now();

            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("signUp@no-reply.com")
                    .to(form.getEmail())
                    .subject("verification email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(),"customer", code))
                    .build();

            mailgunClient.sendEMail(sendMailForm);
            signUpCustomerService.changeCustomerValidationEmail(c.getId(),code);
            return "회원 가입에 성공하였습니다.";
        }
    }


    private String getRandomCode(){
        return RandomStringUtils.random(10,true,true);
    }

    private String getVerificationEmailBody(String email, String name,String  type, String code){
        StringBuilder builder= new StringBuilder();
        return builder.append("Hello ").append(name).append("! PLEASE CLICK LINK FOR VERIFICATION \n\n")
                .append("http://localhost:8081/signup/"+type+"/verify?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}
