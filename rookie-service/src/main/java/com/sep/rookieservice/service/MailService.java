package com.sep.rookieservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordBaseUrl;

    public void sendResetPasswordEmail(String to, String token) {
        String link = UriComponentsBuilder.fromHttpUrl(resetPasswordBaseUrl)
                .queryParam("token", token)
                .build()
                .toUriString();

        String subject = "Reset your password";
        String html = """
            <div style="font-family:Arial,Helvetica,sans-serif;line-height:1.6">
              <h2>Reset your password</h2>
              <p>Nhấn nút dưới đây để đặt lại mật khẩu (hiệu lực 15 phút):</p>
              <p>
                <a href="%s" style="display:inline-block;padding:10px 16px;background:#2563eb;color:#fff;text-decoration:none;border-radius:6px"
                   target="_blank" rel="noopener">Đặt lại mật khẩu</a>
              </p>
              <p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>
              <hr/>
              <p style="font-size:12px;color:#6b7280">Hoặc dán liên kết vào trình duyệt:<br>%s</p>
            </div>
            """.formatted(link, link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Không thể gửi email reset mật khẩu", e);
        }
    }
}
