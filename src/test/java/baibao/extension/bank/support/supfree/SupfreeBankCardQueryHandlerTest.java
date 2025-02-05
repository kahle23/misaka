package baibao.extension.bank.support.supfree;

import baibao.extension.bank.BankCard;
import baibao.extension.bank.BankCardQuery;
import com.alibaba.fastjson.JSON;
import kunlun.action.ActionUtils;
import kunlun.data.json.JsonUtils;
import kunlun.data.json.support.FastJsonHandler;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SupfreeBankCardQueryHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(SupfreeBankCardQueryHandlerTest.class);
    private static final String BANK_CARD_NAME = "bank-card-supfree";

    @Test
    public void test1() {
        JsonUtils.registerHandler("default", new FastJsonHandler());
        ActionUtils.registerAction(BANK_CARD_NAME, new SupfreeBankCardAction());

        BankCardQuery cardQuery = new BankCardQuery("622600687501042806");
        BankCard bankCard = ActionUtils.execute(BANK_CARD_NAME, cardQuery);
        log.info("{}", JSON.toJSONString(bankCard, true));

        cardQuery = new BankCardQuery("6230960288002899254");
        BankCard bankCard1 = ActionUtils.execute(BANK_CARD_NAME, cardQuery);
        log.info("{}", JSON.toJSONString(bankCard1, true));

        cardQuery = new BankCardQuery("6217994000264606028");
        BankCard bankCard2 = ActionUtils.execute(BANK_CARD_NAME, cardQuery);
        log.info("{}", JSON.toJSONString(bankCard2, true));

        cardQuery = new BankCardQuery("6230666046001759766");
        BankCard bankCard3 = ActionUtils.execute(BANK_CARD_NAME, cardQuery);
        log.info("{}", JSON.toJSONString(bankCard3, true));
    }

}
