package com.example.meric.knockknockapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//Class is extending AsyncTask because this class is going to perform a networking operation
public class MailSend extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message1 = "Visitors Today: \n";
    private String awayFromHome = "";
    private boolean dialogShower = true;
    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public MailSend(Context context, String email, String subject, String message, String awayFromHome) {
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message1 = message;
        this.awayFromHome = awayFromHome;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        if(awayFromHome.equals("true"))
            Toast.makeText(context, "Mail Sent", Toast.LENGTH_LONG).show();
        else {
            dialogShower = true;
            progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        if(awayFromHome.equals("false")){
            if (dialogShower == true){
                progressDialog.dismiss();
                dialogShower = false;
            }
        }
        else
            if(dialogShower == false)
                Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        final String username = "knockknockapplication@gmail.com";
        final String password = "12341234)a";

        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress("knockknockapplication@gmail.com"));

            message.setRecipients(Message.RecipientType.TO, email);

            message.setSubject(subject);

            BodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            File folder = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/");
            File[] filename = folder.listFiles();

            int personFound = findPerson(message1);

            if(filename.length == 2)
                Toast.makeText(context, "No visitors today...", Toast.LENGTH_LONG).show();

            for (int k = 0; k < filename.length; k++){
                if (filename[k].toString().endsWith(".jpg")) {
                    if(awayFromHome.equals("true") && personFound != -1){
                        String detail = filename[personFound].toString().substring(filename[personFound].getPath().lastIndexOf('/') + 3, filename[personFound].toString().length() - 4);

                        String[] arrOfStr = detail.split("_");
                        String date = arrOfStr[0];
                        String time = arrOfStr[1];
                        String name = arrOfStr[2];
                        String year = date.substring(0, 2);
                        String mh = date.substring(2, 4);
                        String dt = date.substring(4, 6);
                        date = dt + "." + mh + "." + year;

                        String hour = time.substring(0, 2);
                        String min = time.substring(2, 4);
                        String sec = time.substring(4, 6);
                        time = hour + ":" + min + ":" + sec;

                        message1 = "Date: " + date + " " + "Time: " + time + " Name: " + name + " \n";
                        break;
                    }

                    String detail = filename[k].toString().substring(filename[k].getPath().lastIndexOf('/') + 3, filename[k].toString().length() - 4);

                    String[] arrOfStr = detail.split("_");
                    String date = arrOfStr[0];
                    String time = arrOfStr[1];
                    String name = arrOfStr[2];
                    String year = date.substring(0, 2);
                    String mh = date.substring(2, 4);
                    String dt = date.substring(4, 6);
                    date = dt + "." + mh + "." + year;

                    String hour = time.substring(0, 2);
                    String min = time.substring(2, 4);
                    String sec = time.substring(4, 6);
                    time = hour + ":" + min + ":" + sec;

                    message1 += "Date: " + date + " " + "Time: " + time + " Name: " + name + " \n";
                }
            }

            messageBodyPart.setText(message1);

            for (int k = 0; k < filename.length; k++){
                if (filename[k].toString().endsWith(".jpg")) {
                    if(awayFromHome.equals("true") && personFound != -1) {
                        multipart.addBodyPart(messageBodyPart);

                        messageBodyPart = new MimeBodyPart();

                        DataSource source = new FileDataSource(filename[k].toString());
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(filename[personFound].toString());

                        multipart.addBodyPart(messageBodyPart);
                        break;
                    }
                    multipart.addBodyPart(messageBodyPart);

                    messageBodyPart = new MimeBodyPart();

                    DataSource source = new FileDataSource(filename[k].toString());
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename[k].toString());

                    multipart.addBodyPart(messageBodyPart);
                }
            }

            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int findPerson(String name) {
        File folder = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/");
        File[] filename = folder.listFiles();

        String date = getCurrentTimeUsingDate();

        for(int i = 0; i<filename.length;i++){
            if(filename[i].toString().contains(name))
                return i;
        }

        return -1;

    }
    public String getCurrentTimeUsingDate() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String[] arrOfStr = timeStamp.split("_");
        String date = arrOfStr[0];
        String time = arrOfStr[1];

        String year = date.substring(0, 2);
        String mh = date.substring(2, 4);
        String dt = date.substring(4, 6);
        date = dt + "" + mh + "" + year;
        return date;
    }
}