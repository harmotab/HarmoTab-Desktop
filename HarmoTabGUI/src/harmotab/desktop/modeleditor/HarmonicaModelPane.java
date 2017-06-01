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

package harmotab.desktop.modeleditor;

import harmotab.core.*;
import harmotab.element.Tab;
import harmotab.harmonica.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class HarmonicaModelPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur / destructeur
	//
	
	public HarmonicaModelPane(Harmonica harmonica) {
		m_harmonica = harmonica;
		m_labels = new ArrayList<HarmonicaModelDirectionLabel>();
		m_chromaticButtonObserver = new ChromaticButtonObserver();;

		innerPane = new JPanel();
		innerPane.setSize(new Dimension(400, 200));
		innerPane.setBackground(Color.WHITE);
		setViewportView(innerPane);		
		update();
		
		m_modelObserver = new ModelObserver();
		m_harmonica.addObjectListener(m_modelObserver);
		
		setPreferredSize(new Dimension(400, 200));
	}
	
	public void finalize() {
		m_harmonica.removeObjectListener(m_modelObserver);
	}
	

	/**
	 * Mise à jour de l'affichage
	 */
	public void update() {
		makeUI();
		innerPane.revalidate();
		innerPane.repaint();
	}

	/**
	 * Créer et positionne les composants graphiques
	 */
	private void makeUI() {
		m_labels.clear();
		innerPane.removeAll();
		GridBagLayout layout = new GridBagLayout();
		innerPane.setLayout(layout);
		
		int numberOfHoles = m_harmonica.getModel().getNumberOfHoles();
		boolean pushed = m_chromaticButtonObserver.getButtonPushed();
		
		// Labels de direction
		innerPane.add(new DirectionLabel(Localizer.get(i18n.N_FULL_OVERBLOW)), getConstraints(0, 2));
		innerPane.add(new DirectionLabel(Localizer.get(i18n.N_HALF_OVERBLOW)), getConstraints(0, 3));
		innerPane.add(new DirectionLabel(Localizer.get(i18n.N_BLOW)), getConstraints(0, 4));
		innerPane.add(new DirectionLabel(Localizer.get(i18n.N_DRAW)), getConstraints(0, 6));
		innerPane.add(new DirectionLabel(Localizer.get(i18n.N_HALF_BEND)), getConstraints(0, 7));
		innerPane.add(new DirectionLabel(Localizer.get(i18n.N_FULL_BEND)), getConstraints(0, 8));
		
		// Labels de num�ro de trou
		for (int i = 1; i <= numberOfHoles; i++) {
			innerPane.add(new HoleLabel(i+""), getConstraints(i + 1, 0));
		}
		
		// Blows
		for (int i = 1; i <= numberOfHoles; i++) {
			HarmonicaModelDirectionLabel fullOverblowLabel = new HarmonicaModelDirectionLabel(m_harmonica, new Tab(i, Tab.BLOW, Tab.FULL_BEND, pushed));
			HarmonicaModelDirectionLabel halfOverblowLabel = new HarmonicaModelDirectionLabel(m_harmonica, new Tab(i, Tab.BLOW, Tab.HALF_BEND, pushed));
			HarmonicaModelDirectionLabel blowLabel = new HarmonicaModelDirectionLabel(m_harmonica, new Tab(i, Tab.BLOW, Tab.NONE, pushed));
			
			innerPane.add(fullOverblowLabel, getConstraints(i + 1, 2));
			innerPane.add(halfOverblowLabel, getConstraints(i + 1, 3));
			innerPane.add(blowLabel, getConstraints(i + 1, 4));
			
			m_labels.add(fullOverblowLabel);
			m_labels.add(halfOverblowLabel);
			m_labels.add(blowLabel);
		}
		
		// Body start
		innerPane.add(new HarmonicaBodyLabel(HarmonicaBodyLabel.START), getConstraints(1, 5));
		// Body
		for (int i = 0; i < numberOfHoles; i++) {
			innerPane.add(new HarmonicaBodyLabel(i+1), getConstraints(i + 2, 5));
		}
		// Body ends
		HarmonicaBodyLabel endLabel = null;
		if (m_harmonica.getModel().getHarmonicaType() == HarmonicaType.CHROMATIC) {
			// Harmonica chromatique
			endLabel = new HarmonicaBodyLabel(
					pushed == true ?
					HarmonicaBodyLabel.END_CHROMA_PUSHED :
					HarmonicaBodyLabel.END_CHROMA_NATURAL);
			endLabel.setActionListener(m_chromaticButtonObserver);
		}
		else {
			// Harmonica diatonique
			endLabel = new HarmonicaBodyLabel(HarmonicaBodyLabel.END_DIATO);
			m_chromaticButtonObserver.setButtonPushed(false);
		}
		innerPane.add(endLabel, getConstraints(numberOfHoles + 2, 5));
		
		// Draws
		for (int i = 1; i <= numberOfHoles; i++) {
			HarmonicaModelDirectionLabel drawLabel = new HarmonicaModelDirectionLabel(m_harmonica, new Tab(i, Tab.DRAW, Tab.NONE, pushed));
			HarmonicaModelDirectionLabel halfBendLabel = new HarmonicaModelDirectionLabel(m_harmonica, new Tab(i, Tab.DRAW, Tab.HALF_BEND, pushed));
			HarmonicaModelDirectionLabel fullBendLabel = new HarmonicaModelDirectionLabel(m_harmonica, new Tab(i, Tab.DRAW, Tab.FULL_BEND, pushed));
			
			innerPane.add(drawLabel, getConstraints(i + 1, 6));
			innerPane.add(halfBendLabel, getConstraints(i + 1, 7));
			innerPane.add(fullBendLabel, getConstraints(i + 1, 8));
			
			m_labels.add(drawLabel);
			m_labels.add(halfBendLabel);
			m_labels.add(fullBendLabel);
		}
	}

	private GridBagConstraints getConstraints(int x, int y) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = x;
		constraints.gridy = y;
		return constraints;
	}

	
	private class ModelObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						update();
					}
				}
			);
		}
	}
	
	private class ChromaticButtonObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			m_pushed = !m_pushed;
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							update();
						}
					}
				);
		}
		
		public void setButtonPushed(boolean pushed) {
			m_pushed = pushed;
		}
		
		public boolean getButtonPushed() {
			return m_pushed;
		}
		
		private boolean m_pushed = false;
	}
	
	private class DirectionLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public DirectionLabel(String text) {
			super(text);
			setHorizontalAlignment(JLabel.RIGHT);
		}
	}
	
	private class HoleLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		
		public HoleLabel(String text) {
			super(text);
			setHorizontalAlignment(JLabel.CENTER);
			setBorder(new EmptyBorder(0, 0, 10, 0));
		}
	}
	
	
	//
	// Attributs
	//
	
	private Harmonica m_harmonica = null;
	private JPanel innerPane = null;
	private ModelObserver m_modelObserver = null;
	private ChromaticButtonObserver m_chromaticButtonObserver = null;
	
	private ArrayList<HarmonicaModelDirectionLabel> m_labels = null;

}

