package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.enums.UserRole;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String fromName;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromEmail,
            @Value("${app.mail.from-name}") String fromName) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    @Async("emailExecutor")
    public void sendRegistrationOtp(String toEmail, String recipientName, String otp) {
        String subject = "Verify Your Restaurant POS Account";
        String html = buildOtpEmail(
                recipientName,
                otp,
                "Welcome to Restaurant POS!",
                "Thank you for registering. Use the OTP below to verify your email address and complete your registration.",
                "This OTP expires in 5 minutes."
        );
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async("emailExecutor")
    public void sendPasswordResetOtp(String toEmail, String recipientName, String otp) {
        String subject = "Password Reset Request - Restaurant POS";
        String html = buildOtpEmail(
                recipientName,
                otp,
                "Password Reset Request",
                "We received a request to reset your password. Use the OTP below to proceed.",
                "This OTP expires in 5 minutes. If you did not request a password reset, please ignore this email."
        );
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async("emailExecutor")
    public void sendWelcomeEmail(String toEmail, String employeeName, String restaurantName, UserRole role, String tempPassword, String loginUrl) {
        String subject = "Welcome to " + restaurantName + " - Your Employee Account Credentials";
        String html = buildWelcomeEmail(employeeName, restaurantName, role.name(), toEmail, tempPassword, loginUrl);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async("emailExecutor")
    public void sendEmployeePasswordResetEmail(String toEmail, String employeeName, String restaurantName, String tempPassword, String loginUrl) {
        String subject = "Your Password Has Been Reset - " + restaurantName;
        String html = buildPasswordResetEmail(employeeName, restaurantName, toEmail, tempPassword, loginUrl);
        sendHtmlEmail(toEmail, subject, html);
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildWelcomeEmail(String name, String restaurantName, String role, String email, String password, String loginUrl) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Welcome to %s</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background:linear-gradient(135deg,#e84a5f,#c0392b);padding:36px 40px;text-align:center;">
                              <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;">🍽️ %s</h1>
                              <p style="margin:8px 0 0;color:#fce4e4;font-size:15px;">Welcome to the Team!</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:40px;">
                              <p style="color:#555;font-size:16px;margin:0 0 10px;">Hi <strong>%s</strong>,</p>
                              <p style="color:#666;font-size:15px;line-height:1.7;margin:0 0 24px;">
                                An account has been created for you at <strong>%s</strong> with the role of <strong>%s</strong>.
                              </p>
                              <div style="background:#f8f9fa;border-left:4px solid #e84a5f;padding:20px;margin:0 0 24px;border-radius:4px;">
                                <p style="margin:0 0 8px;color:#333;font-size:14px;"><strong>Email / Username:</strong> %s</p>
                                <p style="margin:0;color:#333;font-size:14px;"><strong>Temporary Password:</strong> <span style="font-family:monospace;font-weight:bold;color:#e84a5f;">%s</span></p>
                              </div>
                              <p style="color:#666;font-size:14px;line-height:1.6;margin:0 0 24px;">
                                <strong>Instructions:</strong> Please log in using your temporary password. You will be prompted to change your password immediately upon your first login.
                              </p>
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center">
                                    <a href="%s" style="display:inline-block;background:#e84a5f;color:#ffffff;text-decoration:none;padding:14px 32px;border-radius:8px;font-weight:bold;font-size:15px;">Log In to Your Account</a>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f8f9fa;padding:24px 40px;text-align:center;border-top:1px solid #eee;">
                              <p style="margin:0;color:#aaa;font-size:12px;">© Restaurant POS System. All rights reserved.</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(restaurantName, restaurantName, name, restaurantName, role, email, password, loginUrl);
    }

    private String buildPasswordResetEmail(String name, String restaurantName, String email, String password, String loginUrl) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Password Reset</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background:linear-gradient(135deg,#e84a5f,#c0392b);padding:36px 40px;text-align:center;">
                              <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;">🍽️ %s</h1>
                              <p style="margin:8px 0 0;color:#fce4e4;font-size:15px;">Password Reset Notice</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:40px;">
                              <p style="color:#555;font-size:16px;margin:0 0 10px;">Hi <strong>%s</strong>,</p>
                              <p style="color:#666;font-size:15px;line-height:1.7;margin:0 0 24px;">
                                Your account password at <strong>%s</strong> has been reset by your administrator.
                              </p>
                              <div style="background:#f8f9fa;border-left:4px solid #e84a5f;padding:20px;margin:0 0 24px;border-radius:4px;">
                                <p style="margin:0 0 8px;color:#333;font-size:14px;"><strong>Email:</strong> %s</p>
                                <p style="margin:0;color:#333;font-size:14px;"><strong>New Temporary Password:</strong> <span style="font-family:monospace;font-weight:bold;color:#e84a5f;">%s</span></p>
                              </div>
                              <p style="color:#666;font-size:14px;line-height:1.6;margin:0 0 24px;">
                                You must change your password upon your next login.
                              </p>
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center">
                                    <a href="%s" style="display:inline-block;background:#e84a5f;color:#ffffff;text-decoration:none;padding:14px 32px;border-radius:8px;font-weight:bold;font-size:15px;">Log In Now</a>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f8f9fa;padding:24px 40px;text-align:center;border-top:1px solid #eee;">
                              <p style="margin:0;color:#aaa;font-size:12px;">© Restaurant POS System. All rights reserved.</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(restaurantName, name, restaurantName, email, password, loginUrl);
    }

    private String buildOtpEmail(String name, String otp, String heading, String body, String footer) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Restaurant POS</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background:linear-gradient(135deg,#e84a5f,#c0392b);padding:36px 40px;text-align:center;">
                              <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;letter-spacing:1px;">🍽️ Restaurant POS</h1>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:40px;">
                              <p style="color:#555;font-size:16px;margin:0 0 10px;">Hi <strong>%s</strong>,</p>
                              <h2 style="color:#2c3e50;font-size:22px;margin:0 0 16px;">%s</h2>
                              <p style="color:#666;font-size:15px;line-height:1.7;margin:0 0 30px;">%s</p>
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center">
                                    <div style="display:inline-block;background:#fef0f0;border:2px dashed #e84a5f;border-radius:12px;padding:20px 48px;margin:0 0 30px;">
                                      <p style="margin:0 0 6px;color:#999;font-size:12px;text-transform:uppercase;letter-spacing:2px;">Your OTP Code</p>
                                      <p style="margin:0;font-size:42px;font-weight:800;color:#e84a5f;letter-spacing:10px;font-family:monospace;">%s</p>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                              <p style="color:#888;font-size:13px;line-height:1.6;margin:0;">%s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f8f9fa;padding:24px 40px;text-align:center;border-top:1px solid #eee;">
                              <p style="margin:0;color:#aaa;font-size:12px;">© 2024 Restaurant POS by Cresto Studio. All rights reserved.</p>
                              <p style="margin:6px 0 0;color:#aaa;font-size:12px;">If you did not request this email, please disregard it.</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(name, heading, body, otp, footer);
    }
}
