<h1>Android Image Encryptor/Decryptor</h1>

<h3>What it is?</h3>

This is an simple Android app that allows the user to encrypt and decrypt image. It requires the user to login using his/her password, only when the authentication is verified, the images are decrypted based on the given password. The images can be encrypted in the app by simply selecting (multiple) files, which will then be hashed and stored locally. 

The users credential are also hashed and stored in the internal storage, the process of authentication gets and hashes the name and password provided, and compare the hashed credential with the one stored locally.

<h3>Encryption/Decryption Algorithm</h3>

This program uses the SHA-256 hashing algorithm and the AES encryption standard, and the password is taken to encrypt and decrypt the image. The credential (name and password) is also hashed and stored locally using SHA-256 algorithm.

<h3>The lib/techs used that you may be interested</h3>

<ul>
  <li>Room persistence</li>
  <li>Singleton class for storing the database object</li>
  <li>RecyclerView</li>
  <li>Intent putExtra/getExtra</li>
  <li>FilePicker library adapted from <a href="https://github.com/TutorialsAndroid/FilePicker?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=7663" target="_blank">Akshay Sunil Masram</a> (Thank you :D)</li>
  <li>MessageDigest</li>
  <li>SHA-256</li>
  <li>AES</li>
</ul>

<h3>Demonstration of Functionalities</h3>

Date: 15July2019

![FunctionalitiesDemo](https://user-images.githubusercontent.com/45169791/61251796-60394180-a753-11e9-87a0-168765419dd7.gif)

Enjoy!
