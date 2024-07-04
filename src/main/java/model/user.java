package model;

import java.util.Date;

public class user {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private Date date_created;
    private byte anonymous;
    private byte activated;
    private byte blocked;
    private byte id_role;

    public user(int id, String username, String password, String email, String fullname, Date date_created, byte anonymous, byte activated, byte blocked, byte id_role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.date_created = date_created;
        this.anonymous = anonymous;
        this.activated = activated;
        this.blocked = blocked;
        this.id_role = id_role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public byte getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(byte anonymous) {
        this.anonymous = anonymous;
    }

    public byte getActivated() {
        return activated;
    }

    public void setActivated(byte activated) {
        this.activated = activated;
    }

    public byte getBlocked() {
        return blocked;
    }

    public void setBlocked(byte blocked) {
        this.blocked = blocked;
    }

    public byte getId_role() {
        return id_role;
    }

    public void setId_role(byte id_role) {
        this.id_role = id_role;
    }
}
