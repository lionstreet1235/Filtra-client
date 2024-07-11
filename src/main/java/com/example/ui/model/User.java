package com.example.ui.model;

public class User
{
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    private String id;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private final String date_created;
    private boolean anonymous;
    private boolean activated;
    private boolean blocked;
    private int id_role;

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

    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(boolean blocked)
    {
        this.blocked = blocked;
    }

    public int getId_role()
    {
        return id_role;
    }

    public void setId_role(int id_role)
    {
        this.id_role = id_role;
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


    public User(String id, String fullname, String username, String email, String password, String date_created, boolean anonymous, boolean activated, int id_role)
    {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.date_created = date_created;
        this.anonymous = anonymous;
        this.activated = activated;
        this.id_role = id_role;
    }

    public String toString()
    {
        return "fullname: " + this.fullname + "\n"
                + "username: " + this.username + "\n"
                + "email: " + this.email + "\n"
                + "datecreate: " + this.date_created + "\n";
    }
    public String activatedToString()
    {
        return String.valueOf(this.activated);
    }
}
