package com.redenergy.parser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redenergy.constant.Constant;
import com.redenergy.exception.EUValidationException;
import com.redenergy.exception.NMIValidationException;
import com.redenergy.exception.ParserException;
import com.redenergy.exception.QualityValidationException;
import com.redenergy.model.EnergyUnit;
import com.redenergy.model.MeterRead;
import com.redenergy.model.MeterVolume;
import com.redenergy.model.Quality;
import com.redenergy.util.Util;

import static java.util.Objects.isNull;

/**
 * @author vmalviya Nem12 parser implementation which reads nem12 file and
 *         returns the collection of meterreads.
 */

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

	private final Logger logger = LoggerFactory.getLogger(SimpleNem12Parser.class);

	/**
	 * Parses Simple NEM12 file.
	 *
	 * @param simpleNem12File
	 *            file in Simple NEM12 format
	 * @return Collection of <code>MeterRead</code> that represents the data in
	 *         the given file.
	 */
	@Override
	public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {

		try {

			Stream<String> lines = Files.lines(Paths.get(simpleNem12File.getPath()));
			List<List<String>> values = lines.map(line -> Arrays.asList(line.split(Constant.COMMA)))
					.collect(Collectors.toList());
			validateIfRecordsPresent(simpleNem12File);
			return readRecordsFromFile(values, lines, simpleNem12File);
		} catch (IOException | ParserException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param values
	 * @param lines
	 * @param simpleNem12File
	 * @return
	 * @throws IOException
	 * @throws ParserException
	 */
	private Collection<MeterRead> readRecordsFromFile(List<List<String>> values, Stream<String> lines,
			File simpleNem12File) throws IOException, ParserException {
		List<MeterRead> meterReads = new ArrayList<>();

		if (checkfirstlinecontains100(Files.lines(Paths.get(simpleNem12File.getPath())))
				&& checklastlinecontains900(Files.lines(Paths.get(simpleNem12File.getPath())))) {
			values.forEach(record -> {
				try {
					executeEachLine(record, meterReads);
				} catch (Exception e) {
					logger.error("Exception when executing lines {}", record, e);
				}
			});

		} else {

			throw new ParserException("either first record doesn't have 100 or last record doesn't have 900");

		}

		return meterReads;
	}

	/**
	 * @param simpleNem12File
	 * @throws ParserException
	 */
	private void validateIfRecordsPresent(File simpleNem12File) throws ParserException {
		if (simpleNem12File.length() == 0) {
			throw new ParserException("SimpleNem12 file doesn't have any meter records");
		}

	}

	/**
	 * @param lines
	 * @return
	 */
	private boolean checkfirstlinecontains100(Stream<String> lines) {
		return lines.findFirst().filter(x -> x.contains(Constant.HUNDRED)).isPresent();
	}

	/**
	 * @param lines
	 * @return
	 */
	private boolean checklastlinecontains900(Stream<String> lines) {
		return lines.reduce((first, second) -> second).filter(x -> x.contains(Constant.NINEHUNDRED)).isPresent();
	}

	/**
	 * @param record
	 * @param meterReads
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private MeterRead executeEachLine(List<String> record, List<MeterRead> meterReads)
			throws NumberFormatException, Exception {
		// TODO Auto-generated method stub
		String firstColumn = record.get(0).trim();

		// if first column is 100 or 900 then don't do any calculation
		if (Constant.HUNDRED.equals(firstColumn) || Constant.NINEHUNDRED.equals(firstColumn)) {

		}

		// if first column is 200 then createMeterRecord with adding NMI and EU
		if (Constant.TWOHUNDRED.equals(firstColumn)) {
			MeterRead meterRead = createNewMeterRecord((String[]) record.toArray());
			meterReads.add(meterRead);
			return meterRead;
		}

		// if first column is 300 then append volume to meterRead
		if (Constant.THREEHUNDRED.equals(firstColumn)) {
			// Get the last element from meterreads list and add the meter
			// volume
			MeterRead meterRead = meterReads.get(meterReads.size() - 1);

			if (QualityValidationCheck((String[]) record.toArray())) {
				MeterVolume meterVolume = new MeterVolume(BigDecimal.valueOf(Double.parseDouble(record.get(2))),
						Quality.valueOf(record.get(3)));
				meterRead.appendVolume(Util.parseDate(record.get(1)), meterVolume);
			}
		}

		return null;

	}

	/**
	 * @param records
	 * @return
	 * @throws QualityValidationException
	 */
	private boolean QualityValidationCheck(String[] records) throws QualityValidationException {

		if (isNull(records[3])
				|| (Quality.valueOf(records[3]) != Quality.A) && (Quality.valueOf(records[3]) != Quality.E)) {
			throw new QualityValidationException("Quality Validation Fail, should not be null and shoulld be A or E");
		} else {

			return true;
		}

	}

	/**
	 * Create New Record for Meter Read
	 *
	 * @param record
	 * @return -meter read
	 * @throws EUValidationException
	 * @throws Exception
	 * 
	 */
	private MeterRead createNewMeterRecord(String[] record) throws NMIValidationException, EUValidationException {
		MeterRead meterRead = new MeterRead();
		if (isNull(record[1]) || record[1].length() != 10) {

			throw new NMIValidationException("NMI Validation fail,Nmi Should not be null and should be of length 10");

		} else {
			meterRead.setNmi(record[1]);
		}

		if (isNull(record[2]) || EnergyUnit.valueOf(record[2]) != EnergyUnit.KWH) {

			throw new EUValidationException("Energy Unit Validation Fail , EU should not be null and should be KWH");

		} else {
			meterRead.setEnergyUnit(EnergyUnit.valueOf(record[2]));
		}

		return meterRead;

	}

}
