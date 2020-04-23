package com.curtisnewbie.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Representation of User model, which stores the User login credential
 */
@Entity(tableName = "user")
public class User {

    @PrimaryKey
    @ColumnInfo(name = "username")
    @NonNull
    private String username;

    @ColumnInfo(name = "hash")
    @NonNull
    private byte[] hash;

    @ColumnInfo(name = "salt")
    @NonNull
    private String salt;

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public byte[] getHash() {
        return hash;
    }

    public void setHash(@NonNull byte[] hash) {
        this.hash = hash;
    }

    @NonNull
    public String getSalt() {
        return salt;
    }

    public void setSalt(@NonNull String salt) {
        this.salt = salt;
    }
}
