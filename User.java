package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblUser")
public class User {
    @PrimaryKey
    @NonNull
    private String username = "";
    private String email;
    private String address;
    private String phone;
    private String avatar;
    private String password;
    private boolean quyen;
    private String fullname;
    public User() {
    }

    public User(@NonNull String username, String email, String address, String phone, String avatar, String password, boolean quyen) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
        this.password = password;
        this.quyen = quyen;

    }
    public User(@NonNull String username,String fullname, String email, String address, String phone, String avatar, String password, boolean quyen) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
        this.password = password;
        this.quyen = quyen;
        this.fullname = fullname;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isQuyen() {
        return quyen;
    }

    public void setQuyen(boolean quyen) {
        this.quyen = quyen;
    }


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
