package models;

public class ChatMessageResponse {

        private String reply;

        public ChatMessageResponse() {
            // Default constructor required for Gson
        }

        public ChatMessageResponse(String reply) {
            this.reply = reply;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }
}
