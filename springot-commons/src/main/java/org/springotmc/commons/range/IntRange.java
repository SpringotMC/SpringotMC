/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.springotmc.commons.range;

import org.springotmc.commons.Numbers;
import org.springotmc.commons.RandomNumbers;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Class defining range in ints, may be used to validate numbers.
 */
@Data(staticConstructor = "of")
@SuppressWarnings({"FieldNamingConvention", "QuestionableName", "StandardVariableNames", "BooleanMethodIsAlwaysInverted"})
public class IntRange {

    /**
     * Range from 1 to 1.
     */
    public static final IntRange ONE = new IntRange(1, 1);
    /**
     * Range from 0 to 0.
     */
    public static final IntRange EMPTY = new IntRange(0, 0);
    /**
     * Range from {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}
     */
    public static final IntRange FULL = new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE);

    private final int min;
    private final int max;

    /**
     * @return random value in range.
     */
    public int getRandom() {
        return ((this.max - this.min) == 0) ? this.max : RandomNumbers.nextInt(this.min, this.max);
    }

    /**
     * Returns random value in range.
     *
     * @param random random instance to use.
     * @return random value in range.
     */
    public int getRandom(Random random) {
        return ((this.max - this.min) == 0) ? this.max : RandomNumbers.nextInt(random, this.min, this.max);
    }

    /**
     * @return size of range. (max - min + 1)
     */
    public long size() {
        return (((long) this.max - (long) this.min) + 1L);
    }

    /**
     * Check if given number is in range.
     *
     * @param i number to check.
     * @return true if it is in range
     */
    public boolean isIn(int i) {
        return (i >= this.min) && (i <= this.max);
    }

    /**
     * Return given number if it is in range, or closest value in range.
     * {@code i > max -> max}
     * {@code i < min -> min}
     * {@code else -> i}
     *
     * @param i number to validate.
     * @return closest number in range.
     */
    public int getIn(int i) {
        if (i > this.max) {
            return this.max;
        }
        if (i < this.min) {
            return this.min;
        }
        return i;
    }

    /**
     * Return given number if it is in range, or default value.
     * {@code i > max -> def}
     * {@code i < min -> def}
     * {@code else -> i}
     *
     * @param i   number to validate.
     * @param def default value.
     * @return given number or default value.
     */
    public int getIn(int i, int def) {
        if (!this.isIn(i)) {
            return def;
        }
        return i;
    }

    /**
     * Create range with only gived value in range.
     *
     * @param num min and max of range.
     * @return range with only one value in range.
     */
    public static IntRange fixed(int num) {
        return new IntRange(num, num);
    }

    /**
     * Parses given string to range, string is valid range when contains 2 numbers (second greater than first) and splt char: <br>
     * " - ", " : ", " ; ", ", ", " ", ",", ";", ":", "-"
     *
     * @param string string to parse.
     * @return parsed range or null.
     */
    @Nullable
    public static IntRange valueOf(@Nullable String string) {
        if ((string == null) || string.isEmpty()) {
            return null;
        }
        String[] nums = null;
        int i = 0;
        boolean firstMinus = string.charAt(0) == '-';
        if (firstMinus) {
            string = string.substring(1);
        }
        while ((i < ByteRange.SPLITS.size()) && ((nums == null) || (nums.length != 2))) {
            nums = string.split(ByteRange.SPLITS.get(i++), 2);
        }
        if ((nums == null) || (nums.length != 2)) {
            return null;
        }
        Integer min = Numbers.asInt(firstMinus ? ("-" + nums[0]) : nums[0]);
        if (min == null) {
            return null;
        }
        Integer max = Numbers.asInt(nums[1]);
        if ((max == null) || (min > max)) {
            return null;
        }
        return new IntRange(min, max);
    }
}
