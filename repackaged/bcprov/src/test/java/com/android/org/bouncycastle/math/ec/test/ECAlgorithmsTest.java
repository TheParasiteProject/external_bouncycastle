/* GENERATED SOURCE. DO NOT MODIFY. */
package com.android.org.bouncycastle.math.ec.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.android.org.bouncycastle.asn1.x9.ECNamedCurveTable;
import com.android.org.bouncycastle.asn1.x9.X9ECParameters;
import com.android.org.bouncycastle.asn1.x9.X9ECPoint;
import com.android.org.bouncycastle.crypto.ec.CustomNamedCurves;
import com.android.org.bouncycastle.math.ec.ECAlgorithms;
import com.android.org.bouncycastle.math.ec.ECCurve;
import com.android.org.bouncycastle.math.ec.ECPoint;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

// Android-changed: parameterized the test.
/**
 * @hide This class is not part of the Android public SDK API
 */
@RunWith(Parameterized.class)
public class ECAlgorithmsTest
{
    private static final int SCALE = 4;
    private static final SecureRandom RND = new SecureRandom();


    // BEGIN Android-added: parameterized the test.
    @Parameterized.Parameters(name = "{0}")
    public static String[] getAllX9ECParameters() {
        Set<String> names = new HashSet<>(AllTests.enumToList(ECNamedCurveTable.getNames()));
        names.addAll(AllTests.enumToList(CustomNamedCurves.getNames()));
        return names.toArray(new String[0]);
    }

    @Parameterized.Parameter(0)
    public String name;

    private ArrayList getX9s(String name) {
        ArrayList<X9ECParameters> x9s = new ArrayList<>();

        X9ECParameters x9 = ECNamedCurveTable.getByName(name);
        if (x9 != null)
        {
            addTestCurves(x9s, x9);
        }

        x9 = CustomNamedCurves.getByName(name);
        if (x9 != null)
        {
            addTestCurves(x9s, x9);
        }
        return x9s;
    }
    // END Android-added: parameterized the test.

    @Ignore("secp256r1 is covered by testSumOfMultipliesComplete")
    public void testSumOfMultiplies()
    {
        X9ECParameters x9 = CustomNamedCurves.getByName("secp256r1");
        assertNotNull(x9);
        doTestSumOfMultiplies(x9);
    }

    // TODO Ideally, mark this test not to run by default
    @Test
    public void testSumOfMultipliesComplete()
    {
        // Android-changed: parameterized the test.
        // ArrayList x9s = getTestCurves();
        ArrayList<X9ECParameters> x9s = getX9s(name);
        Iterator it = x9s.iterator();
        while (it.hasNext())
        {
            X9ECParameters x9 = (X9ECParameters)it.next();
            doTestSumOfMultiplies(x9);
        }
    }

    @Ignore("secp256r1 is covered by testSumOfTwoMultipliesComplete")
    public void testSumOfTwoMultiplies()
    {
        X9ECParameters x9 = CustomNamedCurves.getByName("secp256r1");
        assertNotNull(x9);
        doTestSumOfTwoMultiplies(x9);
    }

    // TODO Ideally, mark this test not to run by default
    @Test
    public void testSumOfTwoMultipliesComplete()
    {
        // Android-changed: parameterized the test.
        // ArrayList x9s = getTestCurves();
        ArrayList<X9ECParameters> x9s = getX9s(name);
        Iterator it = x9s.iterator();
        while (it.hasNext())
        {
            X9ECParameters x9 = (X9ECParameters)it.next();
            doTestSumOfTwoMultiplies(x9);
        }
    }

    private void doTestSumOfMultiplies(X9ECParameters x9)
    {
        ECPoint[] points = new ECPoint[SCALE];
        BigInteger[] scalars = new BigInteger[SCALE];
        for (int i = 0; i < SCALE; ++i)
        {
            points[i] = getRandomPoint(x9);
            scalars[i] = getRandomScalar(x9);
        }

        ECPoint u = x9.getCurve().getInfinity();
        for (int i = 0; i < SCALE; ++i)
        {
            u = u.add(points[i].multiply(scalars[i]));

            ECPoint v = ECAlgorithms.sumOfMultiplies(copyPoints(points, i + 1), copyScalars(scalars, i + 1));

            ECPoint[] results = new ECPoint[]{ u, v };
            x9.getCurve().normalizeAll(results);

            assertPointsEqual("ECAlgorithms.sumOfMultiplies is incorrect", results[0], results[1]);
        }
    }

    private void doTestSumOfTwoMultiplies(X9ECParameters x9)
    {
        ECPoint p = getRandomPoint(x9);
        BigInteger a = getRandomScalar(x9);

        for (int i = 0; i < SCALE; ++i)
        {
            ECPoint q = getRandomPoint(x9);
            BigInteger b = getRandomScalar(x9);
            
            ECPoint u = p.multiply(a).add(q.multiply(b));
            ECPoint v = ECAlgorithms.shamirsTrick(p, a, q, b);
            ECPoint w = ECAlgorithms.sumOfTwoMultiplies(p, a, q, b);

            ECPoint[] results = new ECPoint[]{ u, v, w };
            x9.getCurve().normalizeAll(results);

            assertPointsEqual("ECAlgorithms.shamirsTrick is incorrect", results[0], results[1]);
            assertPointsEqual("ECAlgorithms.sumOfTwoMultiplies is incorrect", results[0], results[2]);

            p = q;
            a = b;
        }
    }

    private void assertPointsEqual(String message, ECPoint a, ECPoint b)
    {
        assertEquals(message, a, b);
    }

    private ECPoint[] copyPoints(ECPoint[] ps, int len)
    {
        ECPoint[] result = new ECPoint[len];
        System.arraycopy(ps, 0, result, 0, len);
        return result;
    }

    private BigInteger[] copyScalars(BigInteger[] ks, int len)
    {
        BigInteger[] result = new BigInteger[len];
        System.arraycopy(ks, 0, result, 0, len);
        return result;
    }

    private ECPoint getRandomPoint(X9ECParameters x9)
    {
        return x9.getG().multiply(getRandomScalar(x9));
    }

    private BigInteger getRandomScalar(X9ECParameters x9)
    {
        return new BigInteger(x9.getN().bitLength(), RND);
    }

    private ArrayList getTestCurves()
    {
        ArrayList x9s = new ArrayList();
        Set names = new HashSet(AllTests.enumToList(ECNamedCurveTable.getNames()));
        names.addAll(AllTests.enumToList(CustomNamedCurves.getNames()));

        Iterator it = names.iterator();
        while (it.hasNext())
        {
            String name = (String)it.next();

            X9ECParameters x9 = ECNamedCurveTable.getByName(name);
            if (x9 != null)
            {
                addTestCurves(x9s, x9);
            }

            x9 = CustomNamedCurves.getByName(name);
            if (x9 != null)
            {
                addTestCurves(x9s, x9);
            }
        }
        return x9s;
    }

    private void addTestCurves(ArrayList x9s, X9ECParameters x9)
    {
        ECCurve curve = x9.getCurve();

        int[] coords = ECCurve.getAllCoordinateSystems();
        for (int i = 0; i < coords.length; ++i)
        {
            int coord = coords[i];
            if (curve.getCoordinateSystem() == coord)
            {
                x9s.add(x9);
            }
            else if (curve.supportsCoordinateSystem(coord))
            {
                ECCurve c = curve.configure().setCoordinateSystem(coord).create();
                x9s.add(new X9ECParameters(c, new X9ECPoint(c.importPoint(x9.getG()), false), x9.getN(), x9.getH()));
            }
        }
    }

    // Android-changed: Use JUnit4.
    /*
    public static Test suite()
    {
        return new TestSuite(ECAlgorithmsTest.class);
    }
     */
}
