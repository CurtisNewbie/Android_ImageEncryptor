package com.curtisnewbie.ImageItem;

import java.util.Date;

public class Image {

    private byte[] encryptedData;
    private String name;

    public Image(byte[] data, String name) {
        this.encryptedData = data;
        this.name = name;
    }

    public byte[] getData() {
        return this.encryptedData;
    }

    public String getName() {
        return this.name;
    }
}
