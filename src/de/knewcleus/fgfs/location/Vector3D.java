/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
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
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.location;

public class Vector3D {
	protected final double x;
	protected final double y;
	protected final double z;
	
	public Vector3D() {
		x=y=z=0.0;
	}
	
	public Vector3D(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vector3D(Vector3D original) {
		this.x=original.x;
		this.y=original.y;
		this.z=original.z;
	}

	public static Vector3D crossProduct(Vector3D a, Vector3D b) {
		double x=a.y*b.z-a.z*b.y;
		double y=a.z*b.x-a.x*b.z;
		double z=a.x*b.y-a.y*b.x;
		
		return new Vector3D(x,y,z);
	}
	
	public Vector3D add(Vector3D b) {
		return new Vector3D(x+b.x,y+b.y,z+b.z);
	}
	
	public Vector3D subtract(Vector3D b) {
		return new Vector3D(x-b.x,y-b.y,z-b.z);
	}

	public Vector3D scale(double s) {
		return new Vector3D(x*s,y*s,z*s);
	}
	
	public Vector3D normalise() {
		double len=getLength();
		
		if (len<1E-22) {
			return new Vector3D();
		}
		
		return new Vector3D(x/len,y/len,z/len);
	}

	public double getLength() {
		return Math.sqrt(x*x+y*y+z*z);
	}

	@Override
	public String toString() {
		return "("+x+","+y+","+z+")";
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
}