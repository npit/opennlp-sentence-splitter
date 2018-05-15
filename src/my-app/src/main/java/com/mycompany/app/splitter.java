/*
 * Copyright 2013 SciFY NPO <info@scify.org>.
 *
 * This product is part of the NewSum Free Software.
 * For more information about NewSum visit
 * 
 * 	http://www.scify.gr/site/en/projects/completed/newsum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * If this code or its output is used, extended, re-engineered, integrated, 
 * or embedded to any extent in another software or hardware, there MUST be 
 * an explicit attribution to this work in the resulting source code, 
 * the packaging (where such packaging exists), or user interface 
 * (where such an interface exists). 
 * The attribution must be of the form "Powered by NewSum, SciFY"
 */
package com.mycompany.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.logging.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
// import org.scify.newsum.server.utils.Configuration;
// import org.scify.newsum.server.utils.FUtil;

/**
 * Handles sentence splitting Uses openNLP lib
 *
 * @author George K. <gkiom@scify.org>`
 */
public class splitter{


    private final String TRAINING_FILE_BASE = "SentenceSplitterTraining_";
    private final String SPLITMODEL_BASE = "splitModel_";

    private String sModelFilePath = null;
    private String sTrainingFilePath = null;

    /**
     * Sentence splitter model
     */
    private SentenceModel smSplitter = null;

    private final String sLang;

    /**
     * Actually used in newsum
     *
     * @param conf The configuration module.
     */
    public splitter(String lang) {
        this.sLang = lang;
    }

    /**
     *
     * @param sModelPath The full path where the model file is located
     * @param sTrainingPath The full path where the sample sentences training
     * file is located
     * @param sLang The language used in the sample sentences file.
     */
    public splitter(String sModelPath, String sTrainingPath, String sLang) {
        this.sModelFilePath = sModelPath;
        this.sTrainingFilePath = sTrainingPath;
        this.sLang = sLang;
    }

    public static void main(String[] args){
        splitter sp = new splitter("FR");
        sp.initSplitter("FR", args[0]);
    }
    // public String[] splitToSentences(String sDocument) {
    //     // if splitter model does not exist, load it
    //     if (smSplitter == null) {
    //         initSplitter(this.sLang);
    //     }
    //     String[] saSentences;
    //     SentenceDetectorME sentenceDetector = new SentenceDetectorME(smSplitter);
    //     saSentences = sentenceDetector.sentDetect(sDocument.trim());
    //     return saSentences;
    // }

    /**
     * Initializes the sentence splitter model for a specific language.
     *
     * @param sLang the language of importance.
     */
    public void initSplitter(String sLang, String trainingFile) {
        // Check whether splitter model already exists
        SentenceModel model = null;
        boolean bModelExisted = false;
        File fTrainingFile=null;
        // TODO try own sentence detector factory
        Logger LOGGER = Logger.getAnonymousLogger();
        SentenceDetectorFactory sdf;
        // If the model was not loaded normally
        if (model == null) {
            Charset charset = Charset.forName("UTF-8");
            if (sTrainingFilePath == null) {
                // read splitter training file
                fTrainingFile = new File(trainingFile);
            } else {
                fTrainingFile = new File(trainingFile);
            }
            ObjectStream<String> lineStream;
            try {
                lineStream
                        = new PlainTextByLineStream(new FileInputStream(fTrainingFile), charset);
                ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
                try {
                    try {
                        // init detector factory
                        sdf = SentenceDetectorFactory
                                .create(SentenceDetectorFactory.class.getName(), sLang, true, null, null);
                        // train model
                        model = SentenceDetectorME.train(
                                sLang, sampleStream, sdf, TrainingParameters.defaultParams());
                    } catch (InvalidFormatException ex) {
                        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.WARNING, "Could not create sentence splitter model.", ex);
                        return;
                    }
                } finally {
                    try {
                        sampleStream.close();
                    } catch (IOException ex) {
                        LOGGER.log(Level.WARNING, "Could not create sentence splitter model.", ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        File fTmp=new File(trainingFile + ".model");
        OutputStream modelOut = null;
        boolean bSuccess = false;
        try {
            //File fTmp = File.createTempFile("splitModel", null);
            FileOutputStream fsOut = new FileOutputStream(fTmp);
            modelOut = new BufferedOutputStream(fsOut);
            model.serialize(modelOut);
            bSuccess = true;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Could not create sentence splitter model.", ex);
        } finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException ex) {
                    bSuccess = false;
                    LOGGER.log(Level.SEVERE, "Could not finalize sentence splitter model.", ex);
                }
            }
        }
        if (bSuccess) {
            this.smSplitter = model;
        }
    }
}

