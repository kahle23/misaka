package baibao.extension.splitter.support;

import cn.hutool.core.io.FileUtil;
import kunlun.action.ActionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

@Ignore
public class SimpleTextSplitterTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleTextSplitterTest.class);
    private static final String handlerName = "simple-text-splitter";

    static {
        ActionUtils.registerAction(handlerName, new AbstractSimpleTextSplitter() {
            @Override
            protected Config getConfig(Object input, String operation, Class<?> clazz) {

                return new Config(300);
            }
        });
    }

    @Test
    public void test1() {
        String text = FileUtil.readString("F:\\test\\test.txt", Charset.forName("utf-8"));
        List<String> execute = ActionUtils.execute(handlerName, text);
        for (String str : execute) {
            log.info("chunk: {}", str);
        }
    }

}
