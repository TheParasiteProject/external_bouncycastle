/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.internal.org.bouncycastle.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.operator.InputDecryptor;
import com.android.internal.org.bouncycastle.operator.InputDecryptorProvider;
import com.android.internal.org.bouncycastle.util.io.Streams;

/**
 * Holding class for a PKCS#8 EncryptedPrivateKeyInfo structure.
 * @hide This class is not part of the Android public SDK API
 */
public class PKCS8EncryptedPrivateKeyInfo
{
    private EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;

    private static EncryptedPrivateKeyInfo parseBytes(byte[] pkcs8Encoding)
        throws IOException
    {
        try
        {
            return EncryptedPrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(pkcs8Encoding));
        }
        catch (ClassCastException e)
        {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e)
        {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo)
    {
        this.encryptedPrivateKeyInfo = encryptedPrivateKeyInfo;
    }

    public PKCS8EncryptedPrivateKeyInfo(byte[] encryptedPrivateKeyInfo)
        throws IOException
    {
        this(parseBytes(encryptedPrivateKeyInfo));
    }

    public AlgorithmIdentifier getEncryptionAlgorithm()
    {
        return encryptedPrivateKeyInfo.getEncryptionAlgorithm();
    }

    public byte[] getEncryptedData()
    {
        return encryptedPrivateKeyInfo.getEncryptedData();
    }

    public EncryptedPrivateKeyInfo toASN1Structure()
    {
         return encryptedPrivateKeyInfo;
    }

    public byte[] getEncoded()
        throws IOException
    {
        return encryptedPrivateKeyInfo.getEncoded();
    }

    public PrivateKeyInfo decryptPrivateKeyInfo(InputDecryptorProvider inputDecryptorProvider)
        throws PKCSException
    {
        try
        {
            InputDecryptor decrytor = inputDecryptorProvider.get(encryptedPrivateKeyInfo.getEncryptionAlgorithm());

            ByteArrayInputStream encIn = new ByteArrayInputStream(encryptedPrivateKeyInfo.getEncryptedData());

            return PrivateKeyInfo.getInstance(Streams.readAll(decrytor.getInputStream(encIn)));
        }
        catch (Exception e)
        {
            throw new PKCSException("unable to read encrypted data: " + e.getMessage(), e);
        }
    }
}
