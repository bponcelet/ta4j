package eu.verdelhan.ta4j.indicators.simple;

import eu.verdelhan.ta4j.Decimal;
import org.junit.Before;
import org.junit.Test;

import static eu.verdelhan.ta4j.TATestsUtils.assertDecimalEquals;
import static org.junit.Assert.*;

/**
 * Created by benoitponcelet on 12/11/17.
 */
public class DegreesIndicatorTest {

    private DegreesIndicator degreesIndicator;

    @Before
    public void setUp() {
        FixedIndicator mockIndicator = new FixedIndicator<Decimal>(
                Decimal.valueOf("1.0"),
                Decimal.valueOf("2.0")

        );

        degreesIndicator = new DegreesIndicator(mockIndicator,1);
    }

    @Test
    public void calculate() throws Exception {
        assertDecimalEquals(degreesIndicator.getValue(0), "0");
        assertDecimalEquals(degreesIndicator.getValue(1), "45");
    }

}