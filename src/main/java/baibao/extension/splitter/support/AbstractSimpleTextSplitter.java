/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.splitter.support;

import baibao.extension.splitter.TextSplitRequest;
import baibao.extension.splitter.TextSplitResponse;
import cn.hutool.core.util.StrUtil;
import kunlun.action.AbstractActionHandler;
import kunlun.util.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;

public abstract class AbstractSimpleTextSplitter extends AbstractActionHandler {

    protected abstract Config getConfig(Object input, String operation, Class<?> clazz);

    @Override
    public Object execute(Object input, String strategy, Type type) {
        //
        Assert.isSupport((Class<?>) type, FALSE, TextSplitResponse.class);
        if (input == null) { return Collections.emptyList(); }
        Assert.isSupport(input.getClass(), FALSE, TextSplitRequest.class, String.class);
        //
        List<String> separators = null;
        Integer chunkSize = null;
        String text;
        if (input instanceof TextSplitRequest) {
            TextSplitRequest req = (TextSplitRequest) input;
            separators = req.getSeparators();
            chunkSize = req.getChunkSize();
            text = req.getText();
        }
        else { text = String.valueOf(input); }
        if (StrUtil.isBlank(text)) { return Collections.emptyList(); }
        //
        Config config = getConfig(input, strategy, (Class<?>) type);
        if (separators == null) { separators = config.getSeparators(); }
        if (chunkSize == null) { chunkSize = config.getChunkSize(); }
        TextSplitResponse resp = new TextSplitResponse(new ArrayList<String>());
        // Get existing separator.
        String separator = null;
        for (String str : separators) {
            if (str == null) { continue; }
            if (text.indexOf(str) > ZERO) {
                separator = str; break;
            }
        }
        // Split text.
        List<String> splitTexts = separator != null
                ? Arrays.asList(text.split(separator)) : Collections.singletonList(text);
        for (String splitText : splitTexts) {
            if (StrUtil.isBlank(splitText)) { continue; }
            int length = splitText.length();
            if (length <= chunkSize) {
                resp.getSplitTexts().add(splitText);
                continue;
            }
            int count = length / chunkSize + (length % chunkSize > ZERO ? ONE : ZERO);
            for (int i = ZERO; i < count; i++) {
                int beginIndex = i * chunkSize;
                int endIndex = (i + ONE) * chunkSize;
                if (endIndex > length) { endIndex = length; }
                resp.getSplitTexts().add(text.substring(beginIndex, endIndex));
            }
        }
        // The result.
        return resp;
    }

    public static class Config {
        /**
         * Follow the list to find if a separator is included, split with the first existing separator.
         */
        private List<String> separators;
        /**
         * This is the size of each chunk.
         */
        private Integer chunkSize;

        public Config(List<String> separators, Integer chunkSize) {
            this.separators = separators;
            this.chunkSize = chunkSize;
        }

        public Config(Integer chunkSize) {

            this.chunkSize = chunkSize;
        }

        public Config() {

        }

        public List<String> getSeparators() {

            return separators;
        }

        public void setSeparators(List<String> separators) {

            this.separators = separators;
        }

        public Integer getChunkSize() {

            return chunkSize;
        }

        public void setChunkSize(Integer chunkSize) {

            this.chunkSize = chunkSize;
        }

    }

}
