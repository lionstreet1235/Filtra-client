package com.example.ui.model;

public class User
{
    private String id;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private final String date_created;
    private boolean anonymous;
    private boolean activated;
    private long max_size;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public long getMax_size()
    {
        return max_size;
    }

    public void setMax_size(long max_size)
    {
        this.max_size = max_size;
    }

    public String getDate_created()
    {
        return date_created;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }

    public boolean isAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    public String getFullname()
    {
        return fullname;
    }

    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }


    public User(String id, String fullname, String username, String email, String password, String date_created, boolean anonymous, boolean activated, long max_size)
    {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.date_created = date_created;
        this.anonymous = anonymous;
        this.activated = activated;
        this.max_size = max_size;
    }

    public String toString()
    {
        return "fullname: " + this.fullname + "\n"
                + "username: " + this.username + "\n"
                + "email: " + this.email + "\n"
                + "date created: " + this.date_created + "\n"
                + "activated status: " + this.activated + "\n"
                + "max data: " + this.max_size / Math.pow(1024, 2) + "MB";
    }
    public String activatedToString()
    {
        return String.valueOf(this.activated);
    }
}



