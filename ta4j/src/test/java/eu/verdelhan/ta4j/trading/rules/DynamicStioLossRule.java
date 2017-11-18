package eu.verdelhan.ta4j.trading.rules;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Order;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.mocks.MockTimeSeries;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by benoitponcelet on 18/09/17.
 */
public class DynamicStioLossRule {
    private TradingRecord buyTradingRecord;
    private TradingRecord sellTradingRecord;
    private ClosePriceIndicator closePrice;
    private StopLossRule rule;

    @Before
    public void setUp() {
        buyTradingRecord = new TradingRecord(Order.OrderType.BUY);
        sellTradingRecord = new TradingRecord(Order.OrderType.SELL);
        closePrice = new ClosePriceIndicator(new MockTimeSeries(
                100, 105, 110, 120, 100, 150, 110, 100, 158
        ));
    }

    @Test
    public void isSatisfied() {
        final Decimal tradedAmount = Decimal.ONE;

        // 5% stop-loss
        rule = new StopLossRule(closePrice, Decimal.valueOf("5"));

        assertFalse(rule.isSatisfied(0, null));
        assertFalse(rule.isSatisfied(1, buyTradingRecord));

        // Enter at 114
        buyTradingRecord.enter(2, Decimal.valueOf("114"), tradedAmount);
        assertFalse(rule.isSatisfied(2, buyTradingRecord));
        assertFalse(rule.isSatisfied(3, buyTradingRecord));
        assertTrue(rule.isSatisfied(4, buyTradingRecord));
        // Exit
        buyTradingRecord.exit(5);

        // Enter at 128
        buyTradingRecord.enter(5, Decimal.valueOf("128"), tradedAmount);
        assertFalse(rule.isSatisfied(5, buyTradingRecord));
        assertTrue(rule.isSatisfied(6, buyTradingRecord));
        assertTrue(rule.isSatisfied(7, buyTradingRecord));

        rule = new StopLossRule(closePrice, Decimal.valueOf("-5"));
        sellTradingRecord.enter(5, Decimal.valueOf("150"), tradedAmount);
        assertFalse(rule.isSatisfied(6, sellTradingRecord));
        assertTrue(rule.isSatisfied(8, sellTradingRecord));


    }
}
