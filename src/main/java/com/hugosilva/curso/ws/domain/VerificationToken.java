package com.hugosilva.curso.ws.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Document
public class VerificationToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int EXPIRATION = 60*24;

    @Id
    private String id;
    private String token;

    @DBRef(lazy = true)
    private User user;
    private Date expireDate;

    public VerificationToken() {
    }

    public VerificationToken(String token) {
        this.token = token;
        this.expireDate = calculateExpiryDate(EXPIRATION);
    }

    public VerificationToken(String token, User user) {
        this.user = user;
        this.token = token;
        this.expireDate = calculateExpiryDate(EXPIRATION);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(calendar.getTime().getTime());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expireDate = calculateExpiryDate(EXPIRATION);
    }

}
