import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    static final String senderEmail = "meerasiddesh12@gmail.com";

    static final String appPassword = "djec ujda qigr nnqe";

    public static void sendMatchEmail(
            String receiverEmail,
            String itemName,
            int score
    ) {
      System.out.println("Trying to send email to: " + receiverEmail);

        try {

            Properties props = new Properties();

            props.put("mail.smtp.auth", "true");

            props.put("mail.smtp.starttls.enable", "true");

            props.put("mail.smtp.host", "smtp.gmail.com");

            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(
                    props,

                    new Authenticator() {

                        protected PasswordAuthentication
                        getPasswordAuthentication() {

                            return new PasswordAuthentication(
                                    senderEmail,
                                    appPassword
                            );
                        }
                    }
            );

            Message message = new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(senderEmail)
            );

            message.setRecipients(
                    Message.RecipientType.TO,

                    InternetAddress.parse(receiverEmail)
            );

            message.setSubject(
                    "RecoverAI - Possible Match Found"
            );

            message.setText(
                    "Hello,\n\n" +

                    "RecoverAI found a possible match " +
                    "for your lost item.\n\n" +

                    "Item: " + itemName + "\n" +

                    "Match Confidence: " + score + "%\n\n" +

                    "Please login to RecoverAI " +
                    "to review the recommendation.\n\n" +

                    "RecoverAI Team"
            );

            Transport.send(message);

            System.out.println(
                    "Real email sent successfully."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
public static void sendClaimStatusEmail(
        String receiverEmail,
        String itemName,
        String status
) {
    try {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, appPassword);
                    }
                }
        );

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmail));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(receiverEmail)
        );

        message.setSubject("RecoverAI - Claim " + status);

        message.setText(
                "Hello,\n\n" +
                "Your claim request for item '" + itemName + "' has been " + status + ".\n\n" +
                "Please login to RecoverAI for more details.\n\n" +
                "RecoverAI Team"
        );

        Transport.send(message);

        System.out.println("Claim status email sent to: " + receiverEmail);

    } catch (Exception e) {
        e.printStackTrace();
    }
}