# Android Image Encryptor/Decryptor

This is a simple Android app that allows you to protect your images through encryption. It is just like a *"vault"* for images.

**This app is powered by**:

- Android Platform
- Dagger 2
- <a href="https://github.com/chrisbanes/PhotoView" target="_blank">PhotoView Library</a>

**Some of the Features:**

- Display the encrypted images on the fly.
- Support thumbnails (thumbnails are also encrypted).
- Support *swipe* and *drag-and-drop* gestures in the list.
- Add images from Gallery (or other similar apps).
- Take pictures using Camera (or other similar apps), original image is deleted once it's encrypted.
- Display a zoomable view of the image (powered by PhotoView library).
- Recover the encrypted image and expose it to other Gallery alike apps.
- Support *Share* functionality, with which you can initiate this app to encrypt the image that you selected in other apps.

## Details of Implementation

1. The authentication and authorisation mechanism is essentially that, the username and salt are stored in the app's DB in plaintext. However, the password is not persisted, only the hash of it is stored. The hash is generated as the persudo code below. Only one user can be registered. When both the name and the hash are matched, the user is authenticated.

   **Persudo code ->** `passwordHash = SHA256(concatenate(password, pw_salt))`

2. All images are encrypted using **AES** algorithm. Images are encrypted and stored in the App's **internal storage**. The key used for encryption is neither stored in local files nor in DB. It's generated in runtime when the user is successfully authenticated. The key is generated as the persudo code below. Note that the salt used for generating the key for image encryption/decryption is different from the one used for hashing password. Two different salts are used.

   **Persudo code ->** `imgKey = SHA256(concatenate(password, img_salt))`

3. When a user browses the images, the images are decrypted in memory. No actual files are created during this process. However, one issue may be involved in this approach, the size of the image may exceed the one allowed by OpenGL ES3.0. The adapted solution to this is to downscale the image when displaying it.

## Additional Hints For Usage

- The first time you use this app, the app expects you to register. You simply enter the username and password that you want to use. Please make sure you remember it, since there is no way to recover it.
- Once you have encrypted some images, and you want to delete some of them. You just swipe the image name (from right to left) in the list, a dialog will pop up and ask you whether you want to delete it. Press "Yes" to delete it.
- In the image list, you can drag and drop (long press first then move it) the image names to change their positions.
- When you are viewing an image, and you want to zoom in/out the image, you just do the gesture that you will normally do on any other apps.
- When you are viewing an image, and you want to recover it back to your normal Gallery apps, you just long press the image, and a dialog will pop up and ask you whether you want to recover it. Notice that you will be asked to grant permission for this operation. It will fail, if you refuse to grant the permission.
