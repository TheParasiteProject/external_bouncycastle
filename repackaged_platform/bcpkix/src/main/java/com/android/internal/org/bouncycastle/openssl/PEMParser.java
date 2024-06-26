/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.internal.org.bouncycastle.openssl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.cms.ContentInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSAPublicKey;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DSAParameter;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.cert.X509AttributeCertificateHolder;
import com.android.internal.org.bouncycastle.cert.X509CRLHolder;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.pkcs.PKCS10CertificationRequest;
import com.android.internal.org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import com.android.internal.org.bouncycastle.util.io.pem.PemHeader;
import com.android.internal.org.bouncycastle.util.io.pem.PemObject;
import com.android.internal.org.bouncycastle.util.io.pem.PemObjectParser;
import com.android.internal.org.bouncycastle.util.io.pem.PemReader;

/**
 * Class for parsing OpenSSL PEM encoded streams containing
 * X509 certificates, PKCS8 encoded keys and PKCS7 objects.
 * <p>
 * In the case of PKCS7 objects the reader will return a CMS ContentInfo object. Public keys will be returned as
 * well formed SubjectPublicKeyInfo objects, private keys will be returned as well formed PrivateKeyInfo objects. In the
 * case of a private key a PEMKeyPair will normally be returned if the encoding contains both the private and public
 * key definition. CRLs, Certificates, PKCS#10 requests, and Attribute Certificates will generate the appropriate BC holder class.
 * </p>
 * @hide This class is not part of the Android public SDK API
 */
public class PEMParser
    extends PemReader
{
    private final Map parsers = new HashMap();

    /**
     * Create a new PEMReader
     *
     * @param reader the Reader
     */
    public PEMParser(
        Reader reader)
    {
        super(reader);

        parsers.put("CERTIFICATE REQUEST", new PKCS10CertificationRequestParser());
        parsers.put("NEW CERTIFICATE REQUEST", new PKCS10CertificationRequestParser());
        parsers.put("CERTIFICATE", new X509CertificateParser());
        parsers.put("TRUSTED CERTIFICATE", new X509TrustedCertificateParser());
        parsers.put("X509 CERTIFICATE", new X509CertificateParser());
        parsers.put("X509 CRL", new X509CRLParser());
        parsers.put("PKCS7", new PKCS7Parser());
        parsers.put("CMS", new PKCS7Parser());
        parsers.put("ATTRIBUTE CERTIFICATE", new X509AttributeCertificateParser());
        parsers.put("EC PARAMETERS", new ECCurveParamsParser());
        parsers.put("PUBLIC KEY", new PublicKeyParser());
        parsers.put("RSA PUBLIC KEY", new RSAPublicKeyParser());
        parsers.put("RSA PRIVATE KEY", new KeyPairParser(new RSAKeyPairParser()));
        parsers.put("DSA PRIVATE KEY", new KeyPairParser(new DSAKeyPairParser()));
        parsers.put("EC PRIVATE KEY", new KeyPairParser(new ECDSAKeyPairParser()));
        parsers.put("ENCRYPTED PRIVATE KEY", new EncryptedPrivateKeyParser());
        parsers.put("PRIVATE KEY", new PrivateKeyParser());
    }

    /**
     * Read the next PEM object attempting to interpret the header and
     * create a higher level object from the content.
     *
     * @return the next object in the stream, null if no objects left.
     * @throws IOException in case of a parse error.
     */
    public Object readObject()
        throws IOException
    {
        PemObject obj = readPemObject();

        if (obj != null)
        {
            String type = obj.getType();
            if (parsers.containsKey(type))
            {
                return ((PemObjectParser)parsers.get(type)).parseObject(obj);
            }
            else
            {
                throw new IOException("unrecognised object: " + type);
            }
        }

        return null;
    }

    private class KeyPairParser
        implements PemObjectParser
    {
        private final PEMKeyPairParser pemKeyPairParser;

        public KeyPairParser(PEMKeyPairParser pemKeyPairParser)
        {
            this.pemKeyPairParser = pemKeyPairParser;
        }

        /**
         * Read a Key Pair
         */
        public Object parseObject(
            PemObject obj)
            throws IOException
        {
            boolean isEncrypted = false;
            String dekInfo = null;
            List headers = obj.getHeaders();

            for (Iterator it = headers.iterator(); it.hasNext();)
            {
                PemHeader hdr = (PemHeader)it.next();

                if (hdr.getName().equals("Proc-Type") && hdr.getValue().equals("4,ENCRYPTED"))
                {
                    isEncrypted = true;
                }
                else if (hdr.getName().equals("DEK-Info"))
                {
                    dekInfo = hdr.getValue();
                }
            }

            //
            // extract the key
            //
            byte[] keyBytes = obj.getContent();

            try
            {
                if (isEncrypted)
                {
                    StringTokenizer tknz = new StringTokenizer(dekInfo, ",");
                    String dekAlgName = tknz.nextToken();
                    byte[] iv = Hex.decode(tknz.nextToken());

                    return new PEMEncryptedKeyPair(dekAlgName, iv, keyBytes, pemKeyPairParser);
                }

                return pemKeyPairParser.parse(keyBytes);
            }
            catch (IOException e)
            {
                if (isEncrypted)
                {
                    throw new PEMException("exception decoding - please check password and data.", e);
                }
                else
                {
                    throw new PEMException(e.getMessage(), e);
                }
            }
            catch (IllegalArgumentException e)
            {
                if (isEncrypted)
                {
                    throw new PEMException("exception decoding - please check password and data.", e);
                }
                else
                {
                    throw new PEMException(e.getMessage(), e);
                }
            }
        }
    }

    private class DSAKeyPairParser
        implements PEMKeyPairParser
    {
        public PEMKeyPair parse(byte[] encoding)
            throws IOException
        {
            try
            {
                ASN1Sequence seq = ASN1Sequence.getInstance(encoding);

                if (seq.size() != 6)
                {
                    throw new PEMException("malformed sequence in DSA private key");
                }

                //            ASN1Integer              v = (ASN1Integer)seq.getObjectAt(0);
                ASN1Integer p = ASN1Integer.getInstance(seq.getObjectAt(1));
                ASN1Integer q = ASN1Integer.getInstance(seq.getObjectAt(2));
                ASN1Integer g = ASN1Integer.getInstance(seq.getObjectAt(3));
                ASN1Integer y = ASN1Integer.getInstance(seq.getObjectAt(4));
                ASN1Integer x = ASN1Integer.getInstance(seq.getObjectAt(5));

                return new PEMKeyPair(
                    new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(p.getValue(), q.getValue(), g.getValue())), y),
                    new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(p.getValue(), q.getValue(), g.getValue())), x));
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new PEMException(
                    "problem creating DSA private key: " + e.toString(), e);
            }
        }
    }

    private class ECDSAKeyPairParser
        implements PEMKeyPairParser
    {
        public PEMKeyPair parse(byte[] encoding)
            throws IOException
        {
            try
            {
                ASN1Sequence seq = ASN1Sequence.getInstance(encoding);

                com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey pKey = com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(seq);
                AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, pKey.getParameters());
                PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, pKey);

                if (pKey.getPublicKey() != null)
                {
                    SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());

                    return new PEMKeyPair(pubInfo, privInfo);
                }
                else
                {
                    return new PEMKeyPair(null, privInfo);
                }
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new PEMException(
                    "problem creating EC private key: " + e.toString(), e);
            }
        }
    }

    private class RSAKeyPairParser
        implements PEMKeyPairParser
    {
        public PEMKeyPair parse(byte[] encoding)
            throws IOException
        {
            try
            {
                ASN1Sequence seq = ASN1Sequence.getInstance(encoding);

                if (seq.size() != 9)
                {
                    throw new PEMException("malformed sequence in RSA private key");
                }

                com.android.internal.org.bouncycastle.asn1.pkcs.RSAPrivateKey keyStruct = com.android.internal.org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(seq);

                RSAPublicKey pubSpec = new RSAPublicKey(
                    keyStruct.getModulus(), keyStruct.getPublicExponent());

                AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);

                return new PEMKeyPair(new SubjectPublicKeyInfo(algId, pubSpec), new PrivateKeyInfo(algId, keyStruct));
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new PEMException(
                    "problem creating RSA private key: " + e.toString(), e);
            }
        }
    }

    private class PublicKeyParser
        implements PemObjectParser
    {
        public PublicKeyParser()
        {
        }

        public Object parseObject(PemObject obj)
            throws IOException
        {
            return SubjectPublicKeyInfo.getInstance(obj.getContent());
        }
    }

    private class RSAPublicKeyParser
        implements PemObjectParser
    {
        public RSAPublicKeyParser()
        {
        }

        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                RSAPublicKey rsaPubStructure = RSAPublicKey.getInstance(obj.getContent());

                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), rsaPubStructure);
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new PEMException("problem extracting key: " + e.toString(), e);
            }
        }
    }

    private class X509CertificateParser
        implements PemObjectParser
    {
        /**
         * Reads in a X509Certificate.
         *
         * @return the X509Certificate
         * @throws java.io.IOException if an I/O error occured
         */
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                return new X509CertificateHolder(obj.getContent());
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    private class X509TrustedCertificateParser
        implements PemObjectParser
    {
        /**
         * Reads in a X509Certificate.
         *
         * @return the X509Certificate
         * @throws java.io.IOException if an I/O error occured
         */
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                return new X509TrustedCertificateBlock(obj.getContent());
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    private class X509CRLParser
        implements PemObjectParser
    {
        /**
         * Reads in a X509CRL.
         *
         * @return the X509Certificate
         * @throws java.io.IOException if an I/O error occured
         */
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                return new X509CRLHolder(obj.getContent());
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    private class PKCS10CertificationRequestParser
        implements PemObjectParser
    {
        /**
         * Reads in a PKCS10 certification request.
         *
         * @return the certificate request.
         * @throws java.io.IOException if an I/O error occured
         */
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                return new PKCS10CertificationRequest(obj.getContent());
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing certrequest: " + e.toString(), e);
            }
        }
    }

    private class PKCS7Parser
        implements PemObjectParser
    {
        /**
         * Reads in a PKCS7 object. This returns a ContentInfo object suitable for use with the CMS
         * API.
         *
         * @return the X509Certificate
         * @throws java.io.IOException if an I/O error occured
         */
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                ASN1InputStream aIn = new ASN1InputStream(obj.getContent());

                return ContentInfo.getInstance(aIn.readObject());
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing PKCS7 object: " + e.toString(), e);
            }
        }
    }

    private class X509AttributeCertificateParser
        implements PemObjectParser
    {
        public Object parseObject(PemObject obj)
            throws IOException
        {
            return new X509AttributeCertificateHolder(obj.getContent());
        }
    }

    private class ECCurveParamsParser
        implements PemObjectParser
    {
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                Object param = ASN1Primitive.fromByteArray(obj.getContent());

                if (param instanceof ASN1ObjectIdentifier)
                {
                    return ASN1Primitive.fromByteArray(obj.getContent());
                }
                else if (param instanceof ASN1Sequence)
                {
                    return X9ECParameters.getInstance(param);
                }
                else
                {
                    return null;  // implicitly CA
                }
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new PEMException("exception extracting EC named curve: " + e.toString());
            }
        }
    }

    private class EncryptedPrivateKeyParser
        implements PemObjectParser
    {
        public EncryptedPrivateKeyParser()
        {
        }

        /**
         * Reads in an EncryptedPrivateKeyInfo
         *
         * @return the X509Certificate
         * @throws java.io.IOException if an I/O error occured
         */
        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(obj.getContent()));
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + e.toString(), e);
            }
        }
    }

    private class PrivateKeyParser
        implements PemObjectParser
    {
        public PrivateKeyParser()
        {
        }

        public Object parseObject(PemObject obj)
            throws IOException
        {
            try
            {
                return PrivateKeyInfo.getInstance(obj.getContent());
            }
            catch (Exception e)
            {
                throw new PEMException("problem parsing PRIVATE KEY: " + e.toString(), e);
            }
        }
    }
}
