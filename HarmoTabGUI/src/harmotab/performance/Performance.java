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

package harmotab.performance;

import harmotab.core.HarmoTabObject;
import harmotab.core.HarmoTabObjectEvent;
import harmotab.core.HarmoTabObjectListener;
import harmotab.core.undo.RestoreCommand;
import harmotab.harmonica.Harmonica;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.NotImplementedError;
import java.io.File;


/**
 * Interpr�tation de la partition par un utilisateur
 */
public class Performance extends HarmoTabObject implements HarmoTabObjectListener {
	
	public static final String FILE_ATTR = "file";
	public static final String NAME_ATTR = "name";
	public static final String HARMONICA_ATTR = "harmonica";
	
	
	//
	// Constructeur
	//
	
	public Performance(File file, String name, Harmonica harmonica) {
		setFile(file);
		setName(name);
		setHarmonica(harmonica);
	}
	
	public Performance() {
		this(null, "", new Harmonica());
	}

	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new PerformanceRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public File getFile() {
		return m_file;
	}
	
	public void setFile(File file) {
		m_file = file;
		fireObjectChanged(FILE_ATTR);
	}
	
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		m_name = name;
		fireObjectChanged(NAME_ATTR);
	}
	
	
	public Harmonica getHarmonica() {
		return m_harmonica;
	}
	
	public void setHarmonica(Harmonica harmonica) {
		if (m_harmonica != null)
			m_harmonica.removeObjectListener(this);
		m_harmonica = harmonica;
		m_harmonica.addObjectListener(this);
		fireObjectChanged(HARMONICA_ATTR);
	}
	
	
	//
	// Impl�mentation de HarmoTabObjectListener
	//

	/**
	 * R�action aux modifications des attributs
	 */
	@Override
	public void onObjectChanged(HarmoTabObjectEvent event) {
		if (event.getSource() instanceof Harmonica) {
			fireObjectChanged(HARMONICA_ATTR);
		}
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		throw new NotImplementedError("Serialization not implemented for Performances");
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		throw new NotImplementedError("Serialization not implemented for Performances");
	}
		
	
	// 
	// Attributs
	// 
	
	protected File m_file;
	protected String m_name;
	protected Harmonica m_harmonica;
	
}


/**
 * Commande d'annulation des modifications d'une interpr�tation
 */
class PerformanceRestoreCommand extends Performance implements RestoreCommand {
	
	public PerformanceRestoreCommand(Performance saved) {
		m_saved = saved;
		m_file = m_saved.m_file;
		m_name = m_saved.m_name;
		m_harmonica = m_saved.m_harmonica;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_file != m_file)
			m_saved.setFile(m_file);
		if (m_saved.m_name != m_name)
			m_saved.setName(m_name);
		if (m_saved.m_harmonica != m_harmonica)
			m_saved.setHarmonica(m_harmonica);
	}

	@Override
	public RestoreCommand getInvertCommand() {
		return new PerformanceRestoreCommand(m_saved);
	}
	
	private Performance m_saved;
}

