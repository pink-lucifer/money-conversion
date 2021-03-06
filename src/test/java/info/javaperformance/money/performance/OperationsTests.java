/*
* Copyright 2014 Mikhail Vorontsov
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package info.javaperformance.money.performance;

import info.javaperformance.money.Money;
import info.javaperformance.money.MoneyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * All arithmetic operations
 */
public class OperationsTests {
    private static final int DATA_SIZE = 1000;
    private static final int WARMUP = 20000;
    private static final int ACTUAL_TEST = 10 * 1000 * 1000;


    public static void main(String[] args) {
        new OperationsTests( DATA_SIZE ).runAllTests( WARMUP, ACTUAL_TEST );
    }

    private final Money[] dataLong;
    private final Money[] dataBd;

    public OperationsTests( final int dataSize )
    {
        final Random r = new Random( 123 );
        dataLong = new Money[ dataSize ];
        for ( int i = 0; i < dataLong.length; ++i )
            dataLong[ i ] = MoneyFactory.fromUnits(r.nextInt(100000), 2);

        dataBd = new Money[ dataSize ];
        for ( int i = 0; i < dataBd.length; ++i )
            dataBd[ i ] = MoneyFactory.fromDouble((i + 1) / 10000.0, 1);
    }

    public List<TestResult> runAllTests( final int warmup, final int iters )
    {
        final List<TestResult> res = new ArrayList<TestResult>( 7 );
        testLongAddition( warmup );
        res.add(new TestResult("MoneyLong+MoneyLong", Math.max(testLongAddition(iters), testLongAddition(iters))));

        testLongToBigDecimalAddition(warmup);
        res.add(new TestResult("MoneyLong+MoneyBigDecimal", Math.max(testLongToBigDecimalAddition(iters), testLongToBigDecimalAddition(iters))));

        testBdToBdAddition(warmup);
        res.add(new TestResult("MoneyBigDecimal+MoneyBigDecimal", Math.max(testBdToBdAddition(iters), testBdToBdAddition(iters))));

        testLongMultiplyLong(warmup);
        final TestResult multByLong = new TestResult( "multiply(long)", Math.max( testLongMultiplyLong(iters), testLongMultiplyLong(iters) ));

        testBdMultiplyLong( warmup );
        multByLong.bdRate = Math.max( testBdMultiplyLong(iters), testBdMultiplyLong(iters) );
        res.add( multByLong);

        testLongMultiplyDouble(warmup);
        final TestResult multByDouble = new TestResult( "multiply(double)", Math.max( testLongMultiplyDouble(iters), testLongMultiplyDouble(iters) ));

        testBdMultiplyDouble( warmup );
        multByDouble.bdRate = Math.max( testBdMultiplyDouble( iters ), testBdMultiplyDouble( iters ) );
        res.add( multByDouble );

        testLongDivideLong(warmup);
        final TestResult divideLong = new TestResult( "divide(long)", Math.max(testLongDivideLong(iters), testLongDivideLong(iters)) );

        testBdDivideLong(warmup);
        divideLong.bdRate = Math.max( testBdDivideLong(iters), testBdDivideLong(iters) );
        res.add( divideLong );

        testLongDivideDouble(warmup);
        final TestResult divideDouble = new TestResult( "divide(double)", Math.max(testLongDivideDouble(iters), testLongDivideDouble(iters)) );

        testBdDivideDouble(warmup);
        divideDouble.bdRate = Math.max( testBdDivideDouble(iters), testBdDivideDouble(iters) );
        res.add( divideDouble );

        //no test result for this
        testLongDivisionWithRound(warmup);
        testLongDivisionWithRound( iters );
        testLongDivisionWithRound( iters );

        testLongTruncate( warmup );
        final TestResult trunc = new TestResult( "truncate", Math.max( testLongTruncate( iters ), testLongTruncate( iters ) ) );

        testBdTruncate( warmup );
        trunc.bdRate = Math.max( testBdTruncate( iters ), testBdTruncate( iters ) );
        res.add( trunc );

        return res;
    }

    public long testLongAddition( final int iters )
    {
        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            Money sum = dataLong[ 0 ];
            for ( int j = 1; j < dataLong.length; ++j )
                sum = sum.add( dataLong[ j ] );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to add " + iters + " MoneyLong values = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongToBigDecimalAddition( final int iters )
    {
        final Money bd = MoneyFactory.fromDouble( 0.555, 1 );

        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataLong.length; ++j )
                dataLong[ j ].add( bd );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to add " + iters + " MoneyLong to MoneyBigDecimal values = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testBdToBdAddition( final int iters )
    {
        final Money bd = MoneyFactory.fromDouble( 0.555, 1 );

        final int actualIters = iters / dataBd.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataBd.length; ++j )
                bd.add( dataBd[ j ] );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to add " + iters + " MoneyBigDecimal to MoneyBigDecimal values = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongMultiplyLong( final int iters )
    {
        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataLong.length; ++j )
                dataLong[ j ].multiply( 97 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to multiply " + iters + " MoneyLong values by Long = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongMultiplyDouble( final int iters )
    {
        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataLong.length; ++j )
                dataLong[ j ].multiply( 97.5 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to multiply " + iters + " MoneyLong values by Double = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testBdMultiplyLong( final int iters )
    {
        final int actualIters = iters / dataBd.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataBd.length; ++j )
                dataBd[ j ].multiply( 97 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to multiply " + iters + " MoneyBigDecimal values by Long = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testBdMultiplyDouble( final int iters )
    {
        final int actualIters = iters / dataBd.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataBd.length; ++j )
                dataBd[ j ].multiply( 97.5 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to multiply " + iters + " MoneyBigDecimal values by Double = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongDivideLong( final int iters )
    {
        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataLong.length; ++j )
                dataLong[ j ].divide( 97, 4 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to divide " + iters + " MoneyLong values by Long = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongDivideDouble(final int iters)
    {
        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataLong.length; ++j )
                dataLong[ j ].divide( 97.5, 4 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to divide " + iters + " MoneyLong values by Double = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testBdDivideLong(final int iters)
    {
        final int actualIters = iters / dataBd.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataBd.length; ++j )
                dataBd[ j ].divide( 97, 4 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to divide " + iters + " MoneyBigDecimal values by Long = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testBdDivideDouble(final int iters)
    {
        final int actualIters = iters / dataBd.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataBd.length; ++j )
                dataBd[ j ].divide( 97.5, 4 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to divide " + iters + " MoneyBigDecimal values by Double = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongTruncate( final int iters )
    {
        final int actualIters = iters / dataLong.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataLong.length; ++j )
                dataLong[ j ].truncate( 1 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to truncate " + iters + " MoneyLong values  = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testBdTruncate( final int iters )
    {
        final int actualIters = iters / dataBd.length;
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < actualIters; ++i )
        {
            for ( int j = 0; j < dataBd.length; ++j )
                dataBd[ j ].truncate( 1 );
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iters / ( time / 1000.0 )) / 1000;
        if ( iters > 20000 )
            System.out.println( "Time to truncate " + iters + " MoneyBigDecimal values  = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

    public long testLongDivisionWithRound( final int iterations )
    {
        final int actualIters = iterations / 10000 * 2;
        final long start = System.currentTimeMillis();
        for ( int iters = 0; iters < actualIters; ++iters )
        {
            for( long i = 0; i < 10000; i += 2 )
            {
                final Money src = MoneyFactory.fromUnits( i, 2 );
                src.divide( 2.0, 1 );
            }
        }
        final long time = System.currentTimeMillis() - start;
        final long rate = (long) (1.0 * iterations / ( time / 1000.0 )) / 1000;
        if ( iterations > 20000 )
            System.out.println( "Time to divide " + iterations + " MoneyLong values via the third branch  = " + time / 1000.0 + " sec; rate = " + rate + " Kops/sec");
        return rate;
    }

}
