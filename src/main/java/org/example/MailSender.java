package org.example;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
public class MailSender {
    String emailAddr = "";

    String zawartość = "Twój kod weryfikacyjny to:";
    void uzytkownik()
    {
        Email email = (Email) EmailBuilder.startingBlank().to(emailAddr).withSubject("Kod weryfikacyjny").withPlainText(zawartość).from("kacperryszardwanat@gmail.com").buildEmail();

        Mailer mailer = MailerBuilder.withSMTPServer("smtp.gmail.com",587,"kacperryszardwanat@gmail.com","ycswclwkueybbgve").withTransportStrategy(TransportStrategy.SMTP).buildMailer();
        mailer.sendMail(email);
    }
}


