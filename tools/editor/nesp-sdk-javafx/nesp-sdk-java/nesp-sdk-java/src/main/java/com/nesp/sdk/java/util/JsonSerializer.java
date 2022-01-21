/*
 *
 *   Copyright (c) 2021  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.sdk.java.util;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 12/14/21 1:15 PM
 * Description:
 * JsonSerializer is a {@link StringSerializer} witch to serialize for Json
 **/
public abstract class JsonSerializer extends StringSerializer {

    /**
     * Serialize the origin data to pretty json string, do not recommend to call this method.
     *
     * @param value the origin data
     * @param <T>   the data type of origin data
     * @return Json String pretty
     */
    public abstract <T> String serializePretty(final T value);

}
