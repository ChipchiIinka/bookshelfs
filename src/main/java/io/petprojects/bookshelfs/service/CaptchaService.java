package io.petprojects.bookshelfs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret}")
    private String secret;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String captchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String params = "?secret=" + secret + "&response=" + captchaResponse;
        String url = RECAPTCHA_VERIFY_URL + params;

        try {
            String response = restTemplate.getForObject(url, String.class);
            return response != null && response.contains("\"success\": true");
        } catch (Exception e) {
            return false;
        }
    }
}
