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

package harmotab.sound;


public class MidiConstants {

	public static final int PROGRAM = 192;
	public static final int NOTEON = 144;
	public static final int NOTEOFF = 128;
	public static final int SUSTAIN = 64;
	public static final int REVERB = 91;
	public static final int ON = 0;
	public static final int OFF = 1;
	public static final int MAX_VOLUME = 0x7F;

	public static final int DEFAULT_VELOCITY = 64;
	public static final int DEFAULT_PRESSURE = 64;
	public static final int DEFAULT_BEND = 64;
	public static final int DEFAULT_REVERB = 64;
	
	public static final int BEATS_PER_MINUTE = 60;
	public static final int TICKS_PER_BEAT = 100;
	
	public static final int END_OF_TRACK = 0x2F;
	public static final int VOLUME_CONTROL = 0x7;
	
	public static final int PERCUSSION_CHANNEL = 9;
	public static final int STRONG_BEAT_SOUNDID = 37;
	public static final int LOW_BEAT_SOUNDID = 42;
	
}

