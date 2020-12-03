package com.honeywell.android.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class User {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String userName;
    private String nickName;
    private String password;
    @Generated(hash = 1955825057)
    public User(Long id, @NotNull String userName, String nickName,
            String password) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
    }


    @Generated(hash = 586692638)
    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUserName() {
        return this.userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getNickName() {
        return this.nickName;
    }


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getPassword() {
        return this.password;
    }


    public void setPassword(String password) {
        this.password = password;
    }



}
