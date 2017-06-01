/**
 * This file is part of HarmoTab.
 *
 * @copyright Copyright (c) 2011 HarmoTab
 * @license GPL-3.0
 * 
 * HarmoTab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * HarmoTab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HarmoTab.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author E. Revert (erevert@harmotab.com)
 */

package harmotab.io.harmonica;

import harmotab.core.*;
import harmotab.element.Tab;
import harmotab.harmonica.*;
import harmotab.throwables.OutOfBoundsError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;


public class HarmoTab2HarmonicaModelReader extends HarmonicaModelReader {

	
	public HarmoTab2HarmonicaModelReader(HarmonicaModel model) {
		super(model);
	}	
	
	
	public void read(File file) throws IOException {
		String filename = file.getName();
		if (filename.endsWith(".md")) {
			m_model.setName(filename.substring(0, filename.length()-3));
		}
		else {
			m_model.setName(filename);
		}
		
		try {
			readHarmoTab2HarmonicaModel(new FileInputStream(file));
			strechOcatves();
		} 
		catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}
	
	
	/**
	 * Lecture des fichiers HarmoTab 2.* .md
	 */
	
	private void readHarmoTab2HarmonicaModel(InputStream input) {
		try {
			Reader reader = new InputStreamReader(input);
			Scanner scanner = new Scanner(reader);
			scanner.useDelimiter("fs17 ");
			scanner.next();
			int index = 0;
			while (scanner.hasNext()) {
				if (index++ != 0)
					extractHt2Model(scanner.next());
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void extractHt2Model(String input) {
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter("\'a4");
		
		int index = 0;
		int numberOfHoles = 0;
		boolean[] altFilled = new boolean[6];
		final int octaveOffset = new Height(Height.C, 3).getSoundId();
		
		while (scanner.hasNext()) {
			String field = scanner.next();
			field = field.substring(0, field.length() - 1);
			
			// Si on débute un nouveau trou
			if (index % 6 == 0) {
				// Met à zéro l'indication des trous renseignés ou non
				for (int alt = 0; alt < 6; alt++)
					altFilled[alt] = false;
			}
			
			// Si une valeur est renseignée
			if (field.length() > 0 && field.charAt(0) >= '0') {
				try {
					byte alt = (byte) (index % 6);
					int note = Integer.parseInt(field);
					if (note > 0) {
						altFilled[alt] = true;
						int hole = (index/6)+1;
						if (hole > m_model.getNumberOfHoles())
							m_model.setNumberOfHoles(hole + (hole/2));
						Height height = new Height(note-1 + octaveOffset);
						m_model.setHeight(HarmonicaModel.createTab(hole, alt), height);
					}
				}
				catch (NumberFormatException exception) {
					System.out.println("extractHt2Model NumberFormatException");
				}
				catch (Error error) {
					error.printStackTrace();
				}
			}
			index++;
			
			// Si on termine un trou
			if (index % 6 == 0) {
				boolean filled = false;
				for (int alt = 0; alt < 6; alt++)
					if (altFilled[alt])
						filled = true;
				if (filled)
					numberOfHoles = index / 6;
			}
		}

		// Affectation du nombre de trous du modèle
		m_model.setNumberOfHoles(numberOfHoles);
		
	}
	
	
	/**
	 * Affectation des octave des notes de l'harmonica
	 */
	private void strechOcatves() {
		int numberOfHoles = m_model.getNumberOfHoles();
		
		Height currentHeight = m_model.getHeight(new Tab(1, Tab.BLOW, Tab.NONE));
		int maxSoundId = currentHeight.getSoundId();
		int currentOctave = currentHeight.getOctave();

		for (int hole = 1; hole <= numberOfHoles; hole++) {
			//currentHeight = m_model.getHeight(new Tab(1, Tab.BLOW, Tab.NONE));

			for (byte type = 0; type < 6; type++) {
				Tab currentTab = HarmonicaModel.createTab(hole, type);
				currentHeight = m_model.getHeight(currentTab);
				if (currentHeight != null) {
					
					currentHeight.setOctave(currentOctave);
					int currentSoundId = currentHeight.getSoundId();
					
					if (currentSoundId > maxSoundId+6)
						currentHeight.setOctave(--currentOctave);
					if (currentSoundId < maxSoundId-6)
						currentHeight.setOctave(++currentOctave);
					
					currentSoundId = currentHeight.getSoundId();
					if (currentSoundId > maxSoundId)
						maxSoundId = currentSoundId;
					
					try {
						Height height = new Height(currentSoundId);
						m_model.setHeight(currentTab, height);
					}
					catch (OutOfBoundsError e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	
}
