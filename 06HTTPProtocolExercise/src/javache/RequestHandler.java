package javache;

import javache.http.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class RequestHandler {

    private HttpRequest httpRequest;

    private HttpResponse httpResponse;

    public RequestHandler() {

    }

    public byte[] handleRequest(String requestContent) {
        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        String assetsFolder = System.getProperty("user.dir")
                + "\\src\\javache\\resources\\assets";

        String url = this.httpRequest.getRequestUrl();
        switch (url) {

            case "/":
                try {
                    byte[] fileContents = Files.readAllBytes(Paths.get(assetsFolder + "\\html\\index.html"));
                    return this.Ok(fileContents);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            case "/users/register":

                String email = this.httpRequest.getBodyParameters().get("email");
                String password = this.httpRequest.getBodyParameters().get("password");
                String confirm = this.httpRequest.getBodyParameters().get("confirm");

                if (!password.equals(confirm)) {

                    return this.BadRequest("Password and confirm password don't match!".getBytes());

                }

                try {
                    User existingUser = this.findUserData(email);

                    if (existingUser != null) {

                        return this.BadRequest("User already exists".getBytes());

                    }

                    //Make a new file writer, which won't overwrite the old file, but it will add to the existing one.

                    try (BufferedWriter writer = new BufferedWriter(

                            new FileWriter(System.getProperty("user.dir")
                                    + "\\src\\javache\\resources\\db\\users.txt", true)

                    )) {

                        writer.write(System.lineSeparator() + UUID.randomUUID().toString() + "|" + email + "|" + password);
                    }

                    this.httpResponse.addHeader("Location:", "/assets/html/login.html");
                    return this.Redirect(new byte[0]);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            case "/users/login":

                return this.Ok("<p style = 'color:red'>I am  login! </p>".getBytes());

            case "/users/profile":

                return this.Ok("<p style = 'color:red'>I am profile! </p>".getBytes());

            default:
                String filePath = assetsFolder + url;
                File file = new File(filePath);

                if (!file.exists() || file.isDirectory()) {
                    return this.NotFound(new byte[0]);
                }

                try {
                    if (!file.getCanonicalPath().startsWith(assetsFolder)) {
                        this.BadRequest(new byte[0]);
                    }

                    byte[] fileContents = Files.readAllBytes(
                            Paths.get(filePath)
                    );


                    return this.Ok(fileContents);

                } catch (IOException e) {
                    return this.NotFound(new byte[0]);
                }

        }

        //  //If the request is a resource, try to read it and if this is successful return it's contents.
        //  if (this.httpRequest.isResource()) {
        //      try {
        //          byte[] fileContents = Files.readAllBytes(
        //                  Paths.get(
        //                          System.getProperty("user.dir")
        //                                  + "\\src\\javache\\resources\\assets"
        //                                  + this.httpRequest.getRequestUrl()
        //                  )
        //          );
//
        //          return this.Ok(fileContents);
//
        //          //If it's resource, but not the right one, return bad request.
        //      } catch (IOException e) {
        //          return this.BadRequest(new byte[0]);
        //      }
//
        //      //In every other case, in which the request it's not a resource, returns that is not a resource.
        //  } else {
        //      return this.Ok("<h1>I am not a resource! </h1>".getBytes());
        //  }
    }

    private byte[] Ok(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.Ok);
        this.httpResponse.setContent(content);

        return this.httpResponse.getBytes();
    }

    private byte[] BadRequest(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.BadRequest);
        this.httpResponse.setContent(content);

        return this.httpResponse.getBytes();
    }

    private byte[] NotFound(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.NotFound);
        this.httpResponse.setContent(content);

        return this.httpResponse.getBytes();
    }

    private byte[] Redirect(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.SeeOther);
        this.httpResponse.setContent(content);

        return this.httpResponse.getBytes();
    }

    private byte[] InternalServerError(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.InternalServerError);
        this.httpResponse.setContent(content);

        return this.httpResponse.getBytes();
    }

    private User findUserData(String email) throws IOException {

        String dbPath = System.getProperty("user.dir")
                + "\\src\\resources\\db\\users.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(dbPath))) {

            String line = reader.readLine();
            while (line != null) {
                String[] userData = line.split("\\|");
                if (userData[0].equals(email)) {
                    return new User(userData[0], userData[1]);
                }
                line = reader.readLine();
            }

        }
        return null;
    }

    class User {

        private String name;

        private String password;

        public User(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }


    }
}
