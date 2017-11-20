package itp341.pai.sonali.finalprojectfrontend.model;
import java.util.Date;
import java.util.List;

/**
 * Created by Sonali Pai on 11/10/2017.
 */

public class User {

    //data members
    private int id;
    private String username;
    private String hashedPassword;
    Date registrationDate;
    List<Comment> comments;
    private String timestamp;
    private int status;
    private String error;
    private String exception;
    private String message;
    private String path;
    public User(String username, String password) {

    }
    public int getId()
    {
        return id;
    }
}
