package com.example.datn_toystoryshop;

import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<Void, Void, Void> {
    private String email;
    private String subject;
    private String message;

    public SendMail(String email, String subject, String message) {
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Cấu hình properties cho JavaMail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server
        props.put("mail.smtp.port", "587"); // Cổng SMTP
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Xác thực tài khoản gửi email
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("toystory.shop.datn@gmail.com", "zxkp oaej gxyz oaxc\n"); // Thay bằng email và mật khẩu của bạn
            }
        });

        try {
            // Tạo email
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("toystory.shop.datn@gmail.com")); // Email gửi
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // Email nhận
            mimeMessage.setSubject(subject); // Chủ đề
            mimeMessage.setContent(message, "text/html; charset=utf-8");// Nội dung

            // Gửi email
            Transport.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}