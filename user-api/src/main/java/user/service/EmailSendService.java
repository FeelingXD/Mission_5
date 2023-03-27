package user.service;

import user.client.MailgunClient;
import user.client.mailgun.SendMailForm;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final MailgunClient mailgunClient;

    public Response sendEmail(){
        SendMailForm form= SendMailForm.builder()
                .from("testAccount@no-reply.com")
                .to("wlals425315@naver.com")
                .subject("test subject")
                .text("test text")
                .build();
        return mailgunClient.sendEMail(form);
    }

}
