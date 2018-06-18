// Copyright Red Energy Limited 2017

package com.redenergy.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.redenergy.model.MeterRead;

public interface SimpleNem12Parser {

  /**
   * Parses Simple NEM12 file.
   * 
   * @param simpleNem12File file in Simple NEM12 format
   * @return Collection of <code>MeterRead</code> that represents the data in the given file.
 * @throws IOException 
 * @throws SimpleNemParserException 
   */
  Collection<MeterRead> parseSimpleNem12(File simpleNem12File);

}
