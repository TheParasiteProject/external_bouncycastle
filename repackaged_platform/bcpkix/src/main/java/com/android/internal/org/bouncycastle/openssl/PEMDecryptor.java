/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.internal.org.bouncycastle.openssl;

/**
 * Base interface for decryption operations.
 * @hide This class is not part of the Android public SDK API
 */
public interface PEMDecryptor
{
    /**
     * Decrypt the passed in data using the associated IV and the decryptor's key state.
     *
     * @param data the encrypted data
     * @param iv the initialisation vector associated with the decryption.
     * @return the decrypted data.
     * @throws PEMException in the event of an issue.
     */
    byte[] decrypt(byte[] data, byte[] iv)
        throws PEMException;
}
