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
package de.tudarmstadt.ukp.dkpro.core.matetools;

import is2.data.SentenceData09;
import is2.io.CONLLReader09;
import is2.mtag.Tagger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ModelProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * <p>
 * DKPro Annotator for the MateToolsMorphTagger
 * </p>
 *
 * Required annotations:
 * <ul>
 * <li>Sentence</li>
 * <li>Token</li>
 * <li>Lemma</li>
 * </ul>
 *
 * Generated annotations:
 * <ul>
 * <li>MorphTag</li>
 * </ul>
 *
 *
 */
@TypeCapability(
        inputs = {
                "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
                "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
                "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma"
        },
        outputs = {
                "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme"
        }
)
public class MateMorphTagger
    extends JCasAnnotator_ImplBase
{
    /**
     * Use this language instead of the document language to resolve the model.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    /**
     * Override the default variant used to locate the model.
     */
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;

    /**
     * Load the model from this location instead of locating the model automatically.
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    protected String modelLocation;

    private CasConfigurableProviderBase<Tagger> modelProvider;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);

        modelProvider = new ModelProviderBase<Tagger>()
        {
            {
                setContextObject(MateMorphTagger.this);

                setDefault(ARTIFACT_ID,
                        "${groupId}.matetools-model-morphtagger-${language}-${variant}");
                setDefault(LOCATION,
                        "classpath:/${package}/lib/morphtagger-${language}-${variant}.properties");
                setDefaultVariantsLocation("${package}/lib/morphtagger-default-variants.map");

                setDefault(VARIANT, "default");

                setOverride(LOCATION, modelLocation);
                setOverride(LANGUAGE, language);
                setOverride(VARIANT, variant);
            }

            @Override
            protected Tagger produceResource(URL aUrl)
                throws IOException
            {
                File modelFile = ResourceUtils.getUrlAsFile(aUrl, true);

                return new Tagger(modelFile.getPath()); // create a MorphTagger
            }
        };

    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        CAS cas = jcas.getCas();

        modelProvider.configure(cas);

        try {
            for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
                List<Token> tokens = JCasUtil.selectCovered(Token.class, sentence);

                List<String> forms = new LinkedList<String>();
                forms.add(CONLLReader09.ROOT);
                forms.addAll(JCasUtil.toText(tokens));

                List<String> lemmas = new LinkedList<String>();
                lemmas.add(CONLLReader09.ROOT_LEMMA);
                for (Token token : tokens) {
                    lemmas.add(token.getLemma().getValue());
                }

                SentenceData09 sd = new SentenceData09();
                sd.init(forms.toArray(new String[0]));
                sd.setLemmas(lemmas.toArray(new String[0]));
                String[] morphTags = modelProvider.getResource().apply(sd).pfeats;
                
                for (int i = 0; i < morphTags.length; i++) {
                    Token token = tokens.get(i);
                    Morpheme morpheme = new Morpheme(jcas, token.getBegin(), token.getEnd());
                    morpheme.setMorphTag(morphTags[i]);
                    morpheme.addToIndexes();
                }
            }
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
}
