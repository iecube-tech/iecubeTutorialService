package com.iecube.iecubetutorial.model.email.servcie;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Void> sendSimpleMessage(String to, String subject, String text);
    CompletableFuture<Void> sendHtmlMessage(String to, String subject, String htmlContent);
}
