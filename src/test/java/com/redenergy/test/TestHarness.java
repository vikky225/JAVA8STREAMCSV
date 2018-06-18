// Copyright Red Energy Limited 2017

package com.redenergy.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.redenergy.exception.ParserException;
import com.redenergy.model.MeterRead;
import com.redenergy.parser.SimpleNem12ParserImpl;

/**
 * Simple test harness for trying out SimpleNem12Parser implementation
 */
public class TestHarness {

  public static void main(String[] args) throws IOException, ParserException {
    File simpleNem12File = new File(args[0]);
  
  Collection<MeterRead> meterReads = new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File);
   MeterRead read6123456789 = meterReads.stream().filter(mr -> mr.getNmi().equals("6123456789")).findFirst().get();
   System.out.println(String.format("Total volume for NMI 6123456789 is %f", read6123456789.getTotalVolume()));  // Should be -36.84

   MeterRead read6987654321 = meterReads.stream().filter(mr -> mr.getNmi().equals("6987654321")).findFirst().get();
   System.out.println(String.format("Total volume for NMI 6987654321 is %f", read6987654321.getTotalVolume()));  // Should be 14.33
  }
}
