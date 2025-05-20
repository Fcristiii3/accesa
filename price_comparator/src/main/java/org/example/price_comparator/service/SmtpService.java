package org.example.price_comparator.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpService {

    private JavaMailSender emailSender;

    public void sendAlert(String email,String productName,float price, String store){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Price alert for: "+productName);
        message.setText("Discount on product "+productName+" from :"+store+" with new discoutned price: "+price);
        emailSender.send(message);
    }
}
