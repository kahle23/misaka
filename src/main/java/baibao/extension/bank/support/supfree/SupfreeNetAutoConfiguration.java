/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.bank.support.supfree;

import baibao.extension.bank.BankCardQuery;
import kunlun.action.ActionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Supfree net.
 * @author Kahle
 */
@Configuration
public class SupfreeNetAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SupfreeNetAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        String actionName = "bank-card-supfree";
        ActionUtils.registerAction(actionName, new SupfreeBankCardAction());
        ActionUtils.registerShortcut(BankCardQuery.class, actionName);
    }

    @Override
    public void destroy() throws Exception {
    }

}
