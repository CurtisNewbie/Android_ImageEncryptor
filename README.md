# Android Image Encryptor/Decryptor

This is an simple Android app that allows the user to encrypt images and browse the encrypted images.

**Some of the Features:**

- Support displaying the encrypted images on the fly.
- Support adding images from Gallery (or other similar apps).
- Support taking pictures using Camera (or other similar apps), original image is deleted once it's encrypted.
- Support displaying a zoomable view of the image that is supported by the **PhotoViewer Library**

**Note: PhotoViewer Library is adapted from this GitHub repository <a href="https://github.com/chrisbanes/PhotoView" target="_blank">PhotoView</a>**

## Details of Implementation

1. The authentication and authorisation mechanism is essentially that, the username and salt are stored in the app's DB in plaintext. However, the password is not persisted, only the hash of it is stored. The hash is generated as the persudo code below. Only one user can be registered. When both the name and the hash are matched, the user is authenticated.

   **Persudo code ->** `passwordHash = SHA256(concatenate(password, pw_salt))`

2. All images are encrypted using **AES** algorithm. Images are encrypted and stored in the App's **internal storage**. The key used for encryption is neither stored in local files nor in DB. It's generated in runtime when the user is successfully authenticated. The key is generated as the persudo code below. Note that the salt used for generating the key for image encryption/decryption is different from the one used for hashing password. Two different salts are used.

   **Persudo code ->** `imgKey = SHA256(concatenate(password, img_salt))`

3. When user browse the images, the images are decrypted in memory. No actual files are created during this process. However, one issue may be involved in this approach, the size of the image may exceed the one allowed by OpenGL ES3.0. The adapted solution to this is to downscale the image when displaying it.

## Additional Hints For Usage

- The first time you use this app, the app expects you to register. You simply enter the username and password that you want to use. Please make sure you remember it, since there is no way to recover it.
- Once you have encryped some images, and you want to delete some of them. You just long press the image name in the list, a dialog will pop up and ask you whether you want to delete it. Press "Yes" to delete it.
- When you are browsing the images, and you want to zoom in the image, you can press on the image, a zoomable view will appears. To close this zoomable view, you will need to press the "Back" button. This "Back" button is provided by the system on your device.
