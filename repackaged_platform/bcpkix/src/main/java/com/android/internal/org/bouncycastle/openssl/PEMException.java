/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.internal.org.bouncycastle.openssl;

import java.io.IOException;

/**
 * @hide This class is not part of the Android public SDK API
 */
public class PEMException
    extends IOException
{
    Exception    underlying;

    public PEMException(
        String    message)
    {
        super(message);
    }

    public PEMException(
        String        message,
        Exception    underlying)
    {
        super(message);
        this.underlying = underlying;
    }

    public Exception getUnderlyingException()
    {
        return underlying;
    }


    public Throwable getCause()
    {
        return underlying;
    }
}
