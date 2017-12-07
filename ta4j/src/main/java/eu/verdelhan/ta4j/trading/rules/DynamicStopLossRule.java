/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.verdelhan.ta4j.trading.rules;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

import java.util.HashMap;
import java.util.Map;

/**
 * A stop-loss rule.
 * <p>
 * Satisfied when the close price reaches the loss threshold.
 */
public class DynamicStopLossRule extends AbstractRule {

    /** The close price indicator */
    private ClosePriceIndicator closePrice;

    /** The loss distance */
    private Decimal distance;

    private Trade activeTrade;
    private Decimal currentLimit;

    /**
     * Constructor.
     * @param closePrice the close price indicator
     * @param distance the loss value ex: 0.0005
     */
    public DynamicStopLossRule(ClosePriceIndicator closePrice, Decimal distance) {
        this.closePrice = closePrice;
        this.distance = distance;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        boolean satisfied = false;
        // No trading history or no trade opened, no loss
        if (tradingRecord != null) {
            Trade currentTrade = tradingRecord.getCurrentTrade();
            Boolean isBuy = currentTrade.getEntry().isBuy();
            if (currentTrade.isOpened()) {
                Decimal currentPrice = closePrice.getValue(index);
                Decimal entryPrice = currentTrade.getEntry().getPrice();
                if(activeTrade == null || !activeTrade.equals(currentTrade)) {
                    currentLimit = Decimal.NaN;
                }
                if(activeTrade != null && activeTrade.equals(currentTrade)) {
                    if(isBuy && currentPrice.minus(distance).isGreaterThan(currentLimit)) {
                        currentLimit = currentPrice.minus(distance);
                    } else if (!isBuy && currentPrice.plus(distance).isGreaterThan(currentLimit)){
                        currentLimit = currentPrice.plus(distance);
                    }
                } else {
                    if(currentTrade.getEntry().isBuy()) {
                        currentLimit = currentTrade.getEntry().getPrice().minus(distance);
                    } else {
                        currentLimit = currentTrade.getEntry().getPrice().plus(distance);
                    }
                    activeTrade = currentTrade;
                }

                if(isBuy) {
                    satisfied = currentPrice.isLessThanOrEqual(currentLimit);
                } else {
                    satisfied = currentPrice.isGreaterThanOrEqual(currentLimit);
                }
            }
        }
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }
}
