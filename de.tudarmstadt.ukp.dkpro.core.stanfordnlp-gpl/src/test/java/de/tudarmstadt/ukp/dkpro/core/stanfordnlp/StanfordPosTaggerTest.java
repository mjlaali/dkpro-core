/**
 * Copyright 2007-2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.dkpro.core.stanfordnlp;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceObjectProviderBase;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;
import de.tudarmstadt.ukp.dkpro.core.testing.DkproTestContext;
import de.tudarmstadt.ukp.dkpro.core.testing.TestRunner;

public class StanfordPosTaggerTest
{
	@Test
	public void testEnglish()
		throws Exception
	{
        runTest("en", "This is a test . \n",
				new String[] { "DT",  "VBZ", "DT",  "NN", "." },
				new String[] { "ART", "V",   "ART", "NN", "PUNC" });

        runTest("en", "A neural net . \n",
        		new String[] { "DT",  "JJ",  "NN", "." },
        		new String[] { "ART", "ADJ", "NN", "PUNC" });

        runTest("en", "John is purchasing oranges . \n",
        		new String[] { "NNP", "VBZ", "VBG", "NNS", "." },
        		new String[] { "NP",  "V",   "V",   "NN",  "PUNC" });

        runTest("en", "fast.41", "This is a test . \n",
                new String[] { "DT",  "VBZ", "DT",  "NN", "." },
                new String[] { "ART", "V",   "ART", "NN", "PUNC" });

        runTest("en", "twitter", "A neural net . \n",
                new String[] { "DT",  "JJ",  "NN", "." },
                new String[] { "ART", "ADJ", "NN", "PUNC" });

        runTest("en", "twitter-fast", "John is purchasing oranges . \n",
                new String[] { "NNP", "VBZ", "VBG", "NNS", "." },
                new String[] { "NP",  "V",   "V",   "NN",  "PUNC" });

        runTest("en", "caseless-left3words-distsim", "john is purchasing oranges . \n",
                new String[] { "NNP", "VBZ", "VBG", "NNS", "." },
                new String[] { "NP",  "V",   "V",   "NN",  "PUNC" });

        runTest("en", "wsj-0-18-caseless-left3words-distsim", "john is purchasing oranges . \n",
                new String[] { "NNP", "VBZ", "VBG", "NNS", "." },
                new String[] { "NP",  "V",   "V",   "NN",  "PUNC" });

    }

	@Test
	public void testGerman()
		throws Exception
    {
        runTest("de", "Das ist ein Test .",
        		new String[] { "PDS", "VAFIN", "ART", "NN",   "$."    },
        		new String[] { "PR",  "V",     "ART", "NN",   "PUNC" });

        runTest("de", "hgc", "Das ist ein Test .",
        		new String[] { "PDS", "VAFIN", "ART", "NN",   "$."    },
        		new String[] { "PR",  "V",     "ART", "NN",   "PUNC" });

        runTest("de", "dewac", "Das ist ein Test .",
        		new String[] { "PDS", "VAFIN", "ART", "NN",   "$."    },
        		new String[] { "PR",  "V",     "ART", "NN",   "PUNC" });

        runTest("de", "fast-caseless", "das ist ein test .",
                new String[] { "PDS", "VAFIN", "ART", "NN",   "$."    },
                new String[] { "PR",  "V",     "ART", "NN",   "PUNC" });

        runTest("de", "fast-caseless", "Das ist ein test .",
                new String[] { "PDS", "VAFIN", "ART", "NN",   "$."    },
                new String[] { "PR",  "V",     "ART", "NN",   "PUNC" });

    }

    @Test
    public void testFrench()
        throws Exception
    {
        JCas jcas = runTest("fr", null, "Nous avons besoin d'une phrase par exemple très "
                + "compliqué, qui contient des constituants que de nombreuses dépendances et que "
                + "possible.");

        String[] posMapped = new String[] { "PR", "V", "NN", "PP", "ART", "NN", "PP", "N", "ADV",
                "ADJ", "PUNC", "PR", "V", "ART", "NN", "CONJ", "ART", "ADJ", "NN", "CONJ", "CONJ",
                "ADJ", "PUNC" };

        String[] posOriginal = new String[] { "CLS", "V", "NC", "P", "DET", "NC", "P", "N", "ADV",
                "ADJ", "PUNC", "PROREL", "V", "DET", "NC", "CS", "DET", "ADJ", "NC", "CC", "CS",
                "ADJ", "PUNC" };

        String[] posTags = new String[] { ".$$.", "A", "ADJ", "ADJWH", "ADV", "ADVWH", "C", "CC",
                "CL", "CLO", "CLR", "CLS", "CS", "DET", "DETWH", "ET", "I", "N", "NC", "NPP", "P",
                "PREF", "PRO", "PROREL", "PROWH", "PUNC", "V", "VIMP", "VINF", "VPP", "VPR", "VS" };

        String[] unmappedPos = new String[] { ".$$." };

        AssertAnnotations.assertTagset(POS.class, "corenlp34", posTags, jcas);
        AssertAnnotations.assertTagsetMapping(POS.class, "corenlp34", unmappedPos, jcas);
        AssertAnnotations.assertPOS(posMapped, posOriginal, select(jcas, POS.class));
    }

    @Test
    public void testFrench2()
        throws Exception
    {
        JCas jcas = runTest("fr", null, "La traduction d'un texte du français vers l'anglais.");

        String[] posMapped = new String[] { "ART", "NN", "PP", "ART", "NN", "PP", "NN", "PP",
                "ART", "NN", "PUNC" };

        String[] posOriginal = new String[] { "DET", "NC", "P", "DET", "NC", "P", "NC", "P", "DET",
                "NC", "PUNC" };

        AssertAnnotations.assertPOS(posMapped, posOriginal, select(jcas, POS.class));
    }

    @Test
    @Ignore("Aelius models not compatible with 3.3.1")
    public void testPortuguese()
        throws Exception
    {
        JCas jcas = runTest("pt", null, "Este é um teste . \n");

        String[] posMapped = new String[] { "PROSUB", "V", "ART", "N", "." };

        String[] posOriginal = new String[] { "POS", "POS", "POS", "POS", "POS" };

        AssertAnnotations.assertPOS(posMapped, posOriginal, select(jcas, POS.class));
    }

    @Test
    public void testChinese()
        throws Exception
    {
    	// The rudder often in the wake of the wind round the back of the area.
        runTest("zh", "尾 舵 常 处于 风轮 后面 的 尾流 区里 。",
        		new String[] { "NN", "NN", "AD",  "VV", "NN", "NN", "DEG", "NN", "NN", "PU"   },
        		new String[] { "NN", "NN", "ADJ", "V",  "NN", "NN", "PRT", "NN", "NN", "PUNC" } );

        // The service sector has become an important engine of Guangdong's economic transformation
        // and upgrading.
        runTest("zh", "服务业 成为 广东 经济 转型 升级 的 重要 引擎 。",
        		new String[] { "NN", "VV", "NR", "NN", "VV", "VV", "DEC", "JJ",  "NN", "PU"    },
        		new String[] { "NN", "V",  "NP", "NN", "V",  "V",  "PRT", "ADJ", "NN", "PUNC"  } );

        // How far is China from the world brand?
        runTest("zh", "中国 离 世界 技术 品牌 有 多远 ？",
        		new String[] { "NR", "P",  "NN", "NN", "NN", "VE", "VV", "PU"   } ,
        		new String[] { "NP", "PP", "NN", "NN", "NN", "V",  "V",  "PUNC" } );
    }

    @Test
    public void testArabic()
        throws Exception
    {
    	// Covering the following sub-Saharan countries with vast areas very
        runTest("ar", "تغطي الصحراء الكبرى الدول التالية بمساحات شاسعة جدا",
        		new String[] { "VBP", "DTNN", "DTJJR", "DTNN", "DTJJ", "NNS", "JJ",  "NN"  },
        		new String[] { "V",   "NN",   "ADJ",   "NN",   "ADJ",  "NN",  "ADJ", "NN" });
    }

    @Test
    public void testSpanish()
        throws Exception
    {
        runTest("es", "Esta es una prueba .",
                new String[] { "pd000000", "vsip000", "di0000", "nc0s000", "fp" },
                new String[] { "PR",       "V",       "ART",    "NN",      "PUNC" });
    }

    @Test
    public void testEscaping() throws Exception
    {
        runTest("en", "This is a ( small ) test . \n",
                new String[] { "DT", "VBZ", "DT",  "-LRB-", "JJ",  "-RRB-", "NN", "." },
                new String[] { "ART", "V",  "ART", "PUNC",     "ADJ", "PUNC",     "NN", "PUNC" });
    }

    /**
     * Setup CAS to test parser for the English language (is only called once if an English test is
     * run)
     */
    private JCas runTest(String aLanguage, String aVariant, String aText, Object... aExtraParams)
        throws Exception
    {
        // No automatic downloading from repository during testing. This makes sure we fail if
        // models are not properly added as test dependencies.
        System.setProperty(ResourceObjectProviderBase.PROP_REPO_OFFLINE, "true");

        AnalysisEngineDescription segmenter;

        if ("zh".equals(aLanguage)) {
            segmenter = createEngineDescription(LanguageToolSegmenter.class);
        }
        else {
            segmenter = createEngineDescription(StanfordSegmenter.class);
        }

        Object[] params = new Object[] {
                StanfordPosTagger.PARAM_VARIANT, aVariant,
                StanfordPosTagger.PARAM_PRINT_TAGSET, true };
        params = ArrayUtils.addAll(params, aExtraParams);
        AnalysisEngineDescription parser = createEngineDescription(StanfordPosTagger.class, params);

        AnalysisEngine engine = createEngine(createEngineDescription(segmenter, parser));

        JCas jcas = engine.newJCas();
        jcas.setDocumentLanguage(aLanguage);
        jcas.setDocumentText(aText);
        engine.process(jcas);

        return jcas;
    }
	private void runTest(String language, String testDocument, String[] tags, String[] tagClasses)
			throws Exception
	{
		runTest(language, null, testDocument, tags, tagClasses);
	}

    private void runTest(String language, String variant, String testDocument, String[] tags,
            String[] tagClasses)
        throws Exception
    {
        AnalysisEngine engine = createEngine(StanfordPosTagger.class,
                StanfordPosTagger.PARAM_VARIANT, variant, StanfordPosTagger.PARAM_PRINT_TAGSET,
                true);
        JCas aJCas = TestRunner.runTest(engine, language, testDocument);

        AssertAnnotations.assertPOS(tagClasses, tags, select(aJCas, POS.class));
    }

    @Rule
    public DkproTestContext testContext = new DkproTestContext();
}
