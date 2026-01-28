package com.boxexpress.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.api.url}")
    private String brevoApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendTrackingUpdate(String to, String customerName, String trackingId, String newStatus,
            String trackingUrl) {
        try {
            log.info("Preparing to send tracking update email to: {}", to);

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", brevoApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> body = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "Box Express SV");
            sender.put("email", senderEmail);
            body.put("sender", sender);

            // To
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", to);
            recipient.put("name", customerName);
            body.put("to", List.of(recipient));

            // Subject
            body.put("subject", "Actualización de Paquete - Box Express SV");

            // HTML Content
            String htmlContent = generateHtmlTemplate(customerName, trackingId, newStatus, trackingUrl);
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(brevoApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully to {} via Brevo API.", to);
            } else {
                log.error("Failed to send email to {}. Status: {}. Body: {}", to, response.getStatusCode(),
                        response.getBody());
            }

        } catch (Exception e) {
            log.error("Exception occurred while sending email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String generateHtmlTemplate(String name, String trackingId, String status, String url) {
        // Color Palette: Blue #1e40af, Orange #f97316
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                        .header { background-color: #1e40af; padding: 20px; text-align: center; }
                        .header h1 { color: #ffffff; margin: 0; font-size: 24px; }
                        .content { padding: 30px; color: #374151; }
                        .greeting { font-size: 18px; margin-bottom: 20px; }
                        .status-box { background-color: #eff6ff; border-left: 4px solid #1e40af; padding: 15px; margin: 20px 0; }
                        .status-label { font-size: 14px; color: #6b7280; margin-bottom: 5px; }
                        .status-value { font-size: 20px; font-weight: bold; color: #1e40af; }
                        .tracking-info { margin-bottom: 20px; }
                        .cta-button { display: block; width: 100%; text-align: center; background-color: #f97316; color: #ffffff; padding: 14px 0; border-radius: 6px; text-decoration: none; font-weight: bold; font-size: 16px; margin-top: 30px; }
                        .cta-button:hover { background-color: #ea580c; }
                        .footer { background-color: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #9ca3af; border-top: 1px solid #e5e7eb; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Box Express SV</h1>
                        </div>
                        <div class="content">
                            <div class="greeting">Hola <strong>%s</strong>,</div>
                            <p>Tenemos una actualización sobre tu paquete.</p>

                            <div class="tracking-info">
                                <strong>Número de Rastreo:</strong> %s
                            </div>

                            <div class="status-box">
                                <div class="status-label">Nuevo Estado:</div>
                                <div class="status-value">%s</div>
                            </div>

                            <p>Tu paquete está más cerca. Puedes ver todos los detalles haciendo clic en el botón de abajo.</p>

                            <a href="%s" class="cta-button">Rastrear mi Paquete</a>
                        </div>
                        <div class="footer">
                            © 2026 Box Express de El Salvador<br>
                            Este es un mensaje automático, por favor no respondas a este correo.
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, trackingId, status, url);
    }
}
