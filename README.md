## Time-based One-Time Password POC

This project creates a simple QR code representing a TOTP secret conforming
to https://datatracker.ietf.org/doc/html/rfc4226 and https://datatracker.ietf.org/doc/html/rfc6238

### Generating QR Codes

This POC supports generating QR codes for use with Google Authenticator to register the device with the generated secret and therefore syncing the TOTP codes.

- Run `App.java`
- Notice output in console will output the generated secret used.
- A QR code will be generated with test data and the generated secret under the directory `./otp/{currentTimeMilli}/`.
- Scan this QR code to register Google Authenticator with the generated secret.

Note: This QR code when used in real world scenarios should not be persisted and the generated secret should be stored in a secure location.

### Loading TOTP Configuration From File

Every TOTP configuration that is not already loaded from a file will be persisted as a QR code image and a json file `otp.json` located in the directory `./otp/{currentTimeMilli}`.

To load this same secret into the POC to be used for testing and verifying TOTP codes with the specified secret input a single argument pointing to the absolute file path of the `otp.json` file you want to load into context.

For example: `C:\Users\...\...\totp-poc\otp\1734135111268\otp.json`.

Once loaded the console will output `Loading OTP config from file` with a success result if the provided JSON file was valid. 

This makes it easy to test with a consistent TOTP configuration that you may have already registered with Google Authenticator.

### Testing Time-Based One Time Passwords

Once you've synced Google Authenticator with the above generated QR code and generated secret we can use this POC to verify, as in real world scenarios, that a provided code matches the TOTP generated from the stored secret.

- Run `App.java`.
- The console will prompt for a 6 digit TOTP.
- Press enter to submit and validate the given code.
- A response of `true` or `false` will be returned indicating if a valid TOTP was given for the configured secret.