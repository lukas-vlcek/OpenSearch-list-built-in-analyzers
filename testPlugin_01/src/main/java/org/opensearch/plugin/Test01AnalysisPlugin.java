/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.*;
import org.opensearch.indices.analysis.AnalysisModule;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * AnalysisPlugin that is included and deployed into testing cluster.
 */
public class Test01AnalysisPlugin extends Plugin implements AnalysisPlugin {

//    @Override
//    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
//        return Collections.singletonMap("test_analyzer", TestAnalyzerProvider::new);
//    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return Collections.singletonMap("xx_test_01_tokenizer", TestTokenizer::new);
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        return Collections.singletonMap("xx_test_01_tokenFilter", TestTokenFilter::new);
    }


//    private class TestAnalyzerProvider extends AbstractIndexAnalyzerProvider<Analyzer> {
//        public TestAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
//            super(indexSettings, name, settings);
//        }
//
//        /**
//         * @return
//         */
//        @Override
//        public Analyzer get() {
//            return new Analyzer() {
//                @Override
//                protected TokenStreamComponents createComponents(String fieldName) {
//                    return null;
//                }
//            };
//        }
//    }

    private class TestTokenizer extends AbstractTokenizerFactory {
        /**
         * Ctor.
         * @param indexSettings
         * @param env
         * @param settings
         * @param name
         */
        public TestTokenizer(IndexSettings indexSettings, Environment env, String name, Settings settings) {
            super(indexSettings, settings, name);
        }

        /**
         * @return
         */
        @Override
        public Tokenizer create() {
            return null;
        }
    }

    private class TestTokenFilter extends AbstractTokenFilterFactory {

        /**
         * Ctor.
         * @param indexSettings
         * @param name
         * @param settings
         */
        public TestTokenFilter(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
            super(indexSettings, name, settings);
        }

        /**
         * @param tokenStream
         * @return
         */
        @Override
        public TokenStream create(TokenStream tokenStream) {
            return null;
        }

        /**
         * @param tokenStream
         * @return
         */
        @Override
        public TokenStream normalize(TokenStream tokenStream) {
            return super.normalize(tokenStream);
        }

        /**
         * @return
         */
        @Override
        public boolean breaksFastVectorHighlighter() {
            return super.breaksFastVectorHighlighter();
        }

        /**
         * @param tokenizer
         * @param charFilters
         * @param previousTokenFilters
         * @param allFilters
         * @return
         */
        @Override
        public TokenFilterFactory getChainAwareTokenFilterFactory(TokenizerFactory tokenizer, List<CharFilterFactory> charFilters, List<TokenFilterFactory> previousTokenFilters, Function<String, TokenFilterFactory> allFilters) {
            return super.getChainAwareTokenFilterFactory(tokenizer, charFilters, previousTokenFilters, allFilters);
        }

        /**
         * @return
         */
        @Override
        public TokenFilterFactory getSynonymFilter() {
            return super.getSynonymFilter();
        }

        /**
         * @return
         */
        @Override
        public AnalysisMode getAnalysisMode() {
            return super.getAnalysisMode();
        }
    }

}
