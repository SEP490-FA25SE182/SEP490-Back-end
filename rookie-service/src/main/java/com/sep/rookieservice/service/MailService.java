package com.sep.rookieservice.service;

public interface MailService {
    void sendResetPasswordEmail(String to, String token);
}
