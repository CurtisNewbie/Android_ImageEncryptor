package com.curtisnewbie.ImageItem;

import java.io.FileNotFoundException;
import java.io.IOException;

/** An image should have the methods to decrypt and show the image.
 *
 */
public interface Decryptable {

    public abstract byte[] decrypt(char[] pw) throws IOException;
}
