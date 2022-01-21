/*
 *
 *   Copyright (c) 2020  NESP Technology Corporation. All rights reserved.
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

package com.nesp.sdk.java.email;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-12 下午11:56
 * @project FishMovie
 * @deprecated 需要重构
 **/
@Deprecated
public class EmailUtils {

    public static EmailInfo getEmailInfo(String emailNumber) {
        switch (getEmailType(emailNumber)) {
            case QQ_EMAIL:
                return new EmailInfo(
                        "QQ邮箱"
                        , "https://w.mail.qq.com/"
                        , "https://mail.qq.com/"
                        , "var ic = document.getElementsByClassName(\"top_announce_mobileqq\")[0];ic.parentNode.removeChild(ic)"
                );
            case WANG_YI_163_EMAIL:
                return new EmailInfo(
                        "网易163邮箱"
                        , "https://dl.reg.163.com/ydzj/maildl?product=mail163&pdconf=yddl_mail163_conf&mc=0F6099&curl=https%3A%2F%2Fmail.163.com%2Fentry%2Fcgi%2Fntesdoor%3Ffrom%3Dsmart%26language%3D0%26style%3D11%26allssl%3Dfalse%26destip%3D192.168.193.48%26df%3Dsmart_android"
                        , "https://mail.163.com/"
                        , "var ic = document.getElementsByClassName(\"hd-you\")[0];ic.parentNode.removeChild(ic);"
                );
            case WANG_YI_126_EMAIL:
                return new EmailInfo(
                        "网易126邮箱"
                        , "https://passport.126.com/ydzj/maildl?product=mail126&pdconf=yddl_mail126_conf&mc=146E1F&curl=https%3A%2F%2Fmail.126.com%2Fentry%2Fcgi%2Fntesdoor%3Ffrom%3Dsmart%26language%3D0%26style%3D11%26destip%3D192.168.202.48%26allssl%3Dfalse%26df%3Dsmart_android"
                        , "https://mail.126.com/"
                        , "var ic = document.getElementsByClassName(\"hd-you\")[0];ic.parentNode.removeChild(ic);"
                );
            case WANG_YI_YEAH_NET_EMAIL:
                return new EmailInfo(
                        "网易yeah邮箱"
                        , "https://passport.yeah.net/ydzj/maildl?product=mailyeah&pdconf=yddl_mailyeah_conf&mc=00789A&curl=https%3A%2F%2Fmail.yeah.net%2Fentry%2Fcgi%2Fntesdoor%3Flightweight%3D1%26verifycookie%3D1%26language%3D0%26style%3D11%26destip%3D172.16.85.55%26allssl%3Dfalse%26from%3Dsmart%26df%3Dsmart_android"
                        , "https://www.yeah.net/"
                        , "var ic = document.getElementsByClassName(\"hd-you\")[0];ic.parentNode.removeChild(ic);"
                );
            case SO_HU_EMAIL:
                return new EmailInfo(
                        "搜狐闪电邮箱"
                        , "https://m.mail.sohu.com/"
                        , "https://mail.sohu.com/fe/#/login"
                        , ""
                );
            case SO_HU_VIP_EMAIL:
                return new EmailInfo(
                        "搜狐VIP邮箱"
                        , "https://vip.sohu.com/#/login"
                        , "https://vip.sohu.com/#/login"
                        , ""
                );
            case XIN_LANG_EMAIL:
                return new EmailInfo(
                        "新浪邮箱"
                        , "https://mail.sina.cn/?vt=4"
                        , "https://mail.sina.com.cn/?vt=0"
                        , "var ic = document.getElementById(\"mailFnCoverAndroid\");ic.parentNode.removeChild(ic);"
                );
            case XIN_LANG_VIP_EMAIL:
                return new EmailInfo(
                        "新浪VIP邮箱"
                        , "https://mail.sina.cn/?page=vipmail&vt=4"
                        , "https://vip.sina.com.cn/?vt=0"
                        , "var ic = document.getElementById(\"mailFnCoverAndroid\");ic.parentNode.removeChild(ic);"
                );
            case OUTLOOK_EMAIL:
                return new EmailInfo(
                        "Outlook"
                        , "https://outlook.live.com/owa/#"
                        , "https://outlook.live.com/owa/#"
                        , "var ic = document.getElementsByClassName(\"tnarrowStoreIcon\")[0];ic.parentNode.removeChild(ic)"
                );
            case G_MAIL:
                return new EmailInfo(
                        "Gmail"
                        , "https://mail.google.com/"
                        , "https://mail.google.com/"
                        , ""
                );
            case NULL:
                return null;
            default:
                return null;
        }
    }


    public static EmailType getEmailType(String emailNumber) {
        if (emailNumber.endsWith("@qq.com")) {
            return EmailType.QQ_EMAIL;
        } else if (emailNumber.endsWith("@163.com")) {
            return EmailType.WANG_YI_163_EMAIL;
        } else if (emailNumber.endsWith("@126.com")) {
            return EmailType.WANG_YI_126_EMAIL;
        } else if (emailNumber.endsWith("@yeah.net")) {
            return EmailType.WANG_YI_YEAH_NET_EMAIL;
        } else if (emailNumber.endsWith("@sohu.com")) {
            return EmailType.SO_HU_EMAIL;
        } else if (emailNumber.endsWith("@vip.sohu.com")) {
            return EmailType.SO_HU_VIP_EMAIL;
        } else if (emailNumber.endsWith("@sina.com") || emailNumber.endsWith("@sina.cn")) {
            return EmailType.XIN_LANG_EMAIL;
        } else if (emailNumber.endsWith("@vip.sina.com") || emailNumber.endsWith("@vip.sina.cn")) {
            return EmailType.XIN_LANG_VIP_EMAIL;
        } else if (emailNumber.endsWith("@outlook.com")) {
            return EmailType.OUTLOOK_EMAIL;
        } else if (emailNumber.endsWith("@gmail.com")) {
            return EmailType.G_MAIL;
        } else {
            return EmailType.NULL;
        }
    }

    public enum EmailType {
        QQ_EMAIL,
        WANG_YI_163_EMAIL,
        WANG_YI_126_EMAIL,
        WANG_YI_YEAH_NET_EMAIL,
        SO_HU_EMAIL,
        SO_HU_VIP_EMAIL,
        XIN_LANG_EMAIL,
        XIN_LANG_VIP_EMAIL,
        OUTLOOK_EMAIL,
        G_MAIL,
        NULL
    }

    public static class EmailInfo {
        public String name;
        public String mobileUrl;
        public String pcUrl;
        public String rmMobileEleJs;

        public EmailInfo(String name, String mobileUrl, String pcUrl, String rmMobileEleJs) {
            this.name = name;
            this.mobileUrl = mobileUrl;
            this.pcUrl = pcUrl;
            this.rmMobileEleJs = rmMobileEleJs;
        }
    }
}
