/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie k�nnen es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder sp�teren ver�ffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es n�tzlich sein wird, aber OHNE JEDE
 * GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite Gew�hrleistung der
 * MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License f�r weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.xplane.RawFrequency;

public class Aerodrome implements IAerodrome {
	protected final Point2D geographicPosition;
	protected final Point2D towerPosition;
	protected final float elevation;
	protected final String identification;
	protected final String name;
	protected final Type type;
	protected volatile List<RawFrequency> frequencies;
	
	public Aerodrome(Point2D geographicPosition, Point2D towerPosition, float elevation,
			String identification, String name, Type type) {
		this.geographicPosition = geographicPosition;
		this.towerPosition = towerPosition;
		this.elevation = elevation;
		this.identification = identification;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Point2D getGeographicPosition() {
		return geographicPosition;
	}
	
    @Override
    public Point2D getTowerPosition() {
        return towerPosition;
    }

    @Override
	public float getElevation() {
		return elevation;
	}
	
	@Override
	public String getIdentification() {
		return identification;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return String.format("Aerodrome %s (%s) %s (%+10.6f,%+11.6f) elev %4.0fft",
				identification,
				name,
				type.toString(),
				geographicPosition.getY() / Units.DEG,
				geographicPosition.getX() / Units.DEG,
				elevation / Units.FT);
	}

    @Override
    public void setFrequencies(List<RawFrequency> frequencies) {
        this.frequencies=frequencies;
    }
    @Override
    public List<RawFrequency> getFrequencies() {
        return frequencies;
    }
}