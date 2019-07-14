package com.curtisnewbie.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Credential class is used for Room Persistence API that it represents the table of credential.
 */
@Entity(tableName = "cred_table")
public class Credential {

    public static final String CRED_TABLE_QUERY = "CREATE TABLE cred_table (cred_name TEXT PRIMARY KEY AUTOINCREMENT, cred_pw TEXT);";

    @PrimaryKey
    private String cred_name;

    @ColumnInfo(name= "cred_pw")
    private String cred_pw;


    public String getCred_name() {
        return cred_name;
    }

    public void setCred_name(String cred_name) {
        this.cred_name = cred_name;
    }

    public String getCred_pw() {
        return cred_pw;
    }

    public void setCred_pw(String cred_pw) {
        this.cred_pw = cred_pw;
    }
}
