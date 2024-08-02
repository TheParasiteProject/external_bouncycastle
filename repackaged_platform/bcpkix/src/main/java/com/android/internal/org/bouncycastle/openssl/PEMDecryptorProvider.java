/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.internal.org.bouncycastle.openssl;

import com.android.internal.org.bouncycastle.operator.OperatorCreationException;

/**
 * @hide This class is not part of the Android public SDK API
 */
public interface PEMDecryptorProvider
{
    PEMDecryptor get(String dekAlgName)
        throws OperatorCreationException;
}
