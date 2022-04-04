package com.nesp.fishplugin.installer;

import com.nesp.fishplugin.compiler.Grammar;
import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.core.data.Page2;
import com.nesp.fishplugin.core.data.Plugin2;

import java.util.List;
import java.util.Map;

/**
 * Team: NESP Technology
 *
 * @author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/4/1 11:37 PM
 * Description:
 **/
public abstract class Installer {

    public void install(Plugin2 plugin) throws InstallException {
        if (plugin == null) {
            throw new InstallException("");
        }

        int deviceType = getEnvironmentDeviceType();

        // Parent
        if (plugin.getParent() != null) {
            throw new InstallException("The parent is not be handled");
        }

        Grammar.GrammarCheckResult checkResult = Grammar.checkGrammar(plugin, false);
        if (checkResult.getLevel() == Grammar.GrammarCheckResult.LEVEL_ERROR) {
            throw new InstallException(checkResult.getMessage());
        }

        // Page
        List<Page2> pages = plugin.getPages();
        for (Page2 page : pages) {
            // Page.refUrl
            String refUrl = page.getRefUrl(deviceType);
            if (refUrl != null && refUrl.length() > 0) {
                page.setRefUrl(refUrl);
            }
            // Page.url
            String url = page.getUrl(deviceType);
            if (url != null && url.length() > 0) {
                page.setUrl(url);
            }
            // Page.js
            String js = page.getJs(deviceType);
            if (js != null && js.length() > 0) {
                page.setJs(js);
            }
            // Page.dsl
            Object dsl = page.getDsl(deviceType);
            if (dsl != null) {
                if (dsl instanceof Map<?, ?> && !((Map<?, ?>) dsl).isEmpty()) {
                    page.setDsl(dsl);
                } else {
                    page.setDsl(dsl);
                }
            }
        }
        plugin.applyPages();

        doInstall(plugin);

    }

    protected abstract void doInstall(Plugin2 plugin) throws InstallException;


    public int getEnvironmentDeviceType() {
        return Environment.getShared().getDeviceType();
    }
}
