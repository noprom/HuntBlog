package com.noprom.app.bean;

/**
 * 实体类
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/3/2.
 */
public abstract class Entity extends Base{
    protected int id;

    public int getId() {
        return id;
    }

    protected String cacheKey;

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
}
