package com.noprom.app.bean;

import java.io.Serializable;

/**
 * 实体基类：实现序列化
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/3/2.
 */
public class Base implements Serializable{

    public final static String UTF8 = "UTF-8";
    public final static String NODE_ROOT = "oschina";

    protected Notice notice;
    public Notice getNotice(){return notice;}
    public void setNotice(Notice notice) {
        this.notice = notice;
    }

}
