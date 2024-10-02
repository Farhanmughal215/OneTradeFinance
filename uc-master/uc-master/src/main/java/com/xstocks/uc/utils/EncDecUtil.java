package com.xstocks.uc.utils;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncDecUtil {

    private static final String BTC_ADDRESS_REGEX = "^(bc1|[13])[a-km-zA-HJ-NP-Z1-9]{25,34}$";

    public static Map<String, String> getWalletAddressAndKey(String seed)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,
                   CipherException {

        Map<String, String> result = new HashMap<>();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

        String sPrivatekeyInHex = privateKeyInDec.toString(16);

        WalletFile aWallet = Wallet.createLight(seed, ecKeyPair);
        String sAddress = aWallet.getAddress();

        result.put("address", "0x" + sAddress);
        result.put("privatekey", sPrivatekeyInHex);

        return result;

    }

    public static boolean validateBtcAddress(String address) {

        // Compile the ReGex
        Pattern p = Pattern.compile(BTC_ADDRESS_REGEX);

        // If the str
        // is empty return false
        if (address == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given
        // str  using regular expression.
        Matcher m = p.matcher(address);

        // Return if the MICR Code
        // matched the ReGex
        return m.matches();
    }

    public static boolean validateEthAddress(String address) {
        String regex = "^0x[0-9a-f]{40}$";
        if (address.matches(regex)) {
            return true;
        }
        return false;
    }

    public static boolean checkEthSumAddress(String address) {
        // to fetch the part after 0x
        String subAddr = address.substring(2);
        // Make it to original lower case address
        String subAddrLower = subAddr.toLowerCase();
        // Create a SHA3256 hash (Keccak-256)
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        digestSHA3.update(subAddrLower.getBytes());
        String digestMessage = Hex.toHexString(digestSHA3.digest());
        /*
         * Check each letter is upper case or not if it is upper case then the
         * corresponding binary position of the hashed address should be 1 i.e the
         * message digest letter should be getter than 7 as 7 is the last Hex digit
         * which starts with 0 in binary rest of all 8 to f starts with 1 (i.e 7: 0111, 8: 1000)
         */
        for (short i = 0; i < subAddr.length(); i++) {
            if (subAddr.charAt(i) >= 65 && subAddr.charAt(i) <= 91) {
                String ss = Character.toString(digestMessage.charAt(i));
                if (!(Integer.parseInt(ss, 16) > 7)) {
                    return false;
                }
            }
        }
        return true;

    }
}
