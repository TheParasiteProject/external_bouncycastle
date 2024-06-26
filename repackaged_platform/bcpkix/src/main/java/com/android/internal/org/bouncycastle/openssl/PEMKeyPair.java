/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.internal.org.bouncycastle.openssl;

import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * @hide This class is not part of the Android public SDK API
 */
public class PEMKeyPair
{
    private final SubjectPublicKeyInfo publicKeyInfo;
    private final PrivateKeyInfo privateKeyInfo;

    public PEMKeyPair(SubjectPublicKeyInfo publicKeyInfo, PrivateKeyInfo privateKeyInfo)
    {
        this.publicKeyInfo = publicKeyInfo;
        this.privateKeyInfo = privateKeyInfo;
    }

    public PrivateKeyInfo getPrivateKeyInfo()
    {
        return privateKeyInfo;
    }

    public SubjectPublicKeyInfo getPublicKeyInfo()
    {
        return publicKeyInfo;
    }
}
