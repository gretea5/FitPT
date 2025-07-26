package com.sahur.fitpt.domain.firebase.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class FcmMessageWithData {
    private boolean validate_only;
    private Message message;

    public FcmMessageWithData() {
    }

    public FcmMessageWithData(boolean validate_only, Message message) {
        super();
        this.validate_only = validate_only;
        this.message = message;
    }


    /**
     * Message
     *
     * @author USER1
     */
    @Setter
    @Getter
    public static class Message {
        private Notification notification;
        private String token;
        private Map<String, String> data;

        public Message() {
        }

        public Message(Notification notification, String token) {
            super();
            this.notification = notification;
            this.token = token;
        }

    }

    /**
     * Notification
     *
     * @author USER1
     */
    @Setter
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;

        public Notification() {
        }

        public Notification(String title, String body, String image) {
            super();
            this.title = title;
            this.body = body;
            this.image = image;
        }


    }
}
