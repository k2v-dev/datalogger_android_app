package com.decalthon.helmet.stability.utilities;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 */
public class UniqueKeyGen {
        /**
         * Compute the key by concating the email and phone_num
         * @param email_id
         * @param phone_num
         * @return integer
         */
        private static long genKey(String email_id, String phone_num){
            phone_num = phone_num.replace("+", "");

            String new_str = email_id+phone_num;
            //Below logic produce 1% collision or duplicate, but will take upto 2.5 bytes
            long total = 0;
            for(int i=1 ; i <= new_str.length(); i++){
                total += (new_str.charAt(i-1)) * i; // Multiple the ascii value of character and it's position
            }



//       //Below no collision/duplicate is produce, but total's value is very large 8 bytes or more
//    long total = 0;
//    for (int i = 0; i < new_str.length(); i++) {
//        total = 31*total + (long)(new_str.charAt(i)); // 31 ==> a prime number that is bigger than the wider difference between your characters
//    }


            return total;
        }

    /**
     * Generate 10 character user id by combination of first 2 character
     * @param email_id
     * @param phone_num
     * @return 10 character string
     */
        public static String genUserId_own(String email_id, String phone_num){
            // Generate key
            long key = genKey(email_id.trim().toUpperCase(), phone_num.trim());

            // Remove all special charater from email id
            // and email will be first 2 characters of email id
            String email_id_new = email_id.trim().toUpperCase().replaceAll("[^a-zA-Z0-9]", "");
            String email = email_id_new.substring(0, 2);
//            String email = email_id.trim().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").substring(0, 2);

            //Take middle 2 character of combination of email and phone_num
            String combine = email_id_new+phone_num.trim();
            int mid =  (int)Math.ceil(combine.length()/2.0);
            combine = combine.substring(mid, mid+2);

            //num will be last 2 digit of phone number
            String num = phone_num.trim();
            num = num.substring(num.length()-3);

            // Convert key to base 36 string
            String base36Str = Long.toString(key, 36).toUpperCase();
            String code_str = ("0000" + base36Str).substring(base36Str.length());// give 4 character length padding 0 even the base32str.length < 4

           // String code_str = Common.convertByteArrToStr(longToBytesNew(key), false);

            //most probable that apart from last 5 character of hexstring, all will be zero
            // concate of first 2 characters of email, last 3 character of phone and last 5 character of hexstring
            return email+combine+num+code_str;

//            return email+num+code_str.substring(code_str.length()-5);
        }


    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 128;

    /**
     * It will generate 25 character string by using email id and phone_num
     * @param email_id
     * @param phone_num
     * @return 25 dharacter letter
     */
    public static String genUserId(String email_id, String phone_num) {
        String email_new = email_id.trim().toUpperCase();
        String phone_num_new = phone_num.trim().toUpperCase().replaceAll("[^0-9]", "");
        email_new += phone_num_new;

        String returnValue = null;

        byte[] securePassword = hash(email_new.toCharArray(), phone_num_new.getBytes());

        String generatedPassword = "";
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< securePassword.length ;i++)
        {
            sb.append(Integer.toString((securePassword[i] & 0xff) + 0x100, 16).substring(1));
        }
        //Get complete hashed email_id in hex format
        generatedPassword = sb.toString();
        return new BigInteger(generatedPassword, 16).toString(36).toUpperCase();
    }

    /**
     * Generate hash key using advance hashing algorithm
     * @param password
     * @param salt
     * @return
     */
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }
}

