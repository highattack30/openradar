/**
 * Copyright (C) 2013 Wolfram Wagner
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
package de.knewcleus.openradar.rpvd.contact;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;
import de.knewcleus.openradar.rpvd.contact.ContactShape.Type;

/**
 * This class implements the orginal layout introduced into OR by W.Wagner.
 * It is for from being realistic as it relies on multiplayer protocol data and
 * does not support squawk codes, but it is easier to understand for beginners.
 *
 * @author Wolfram Wagner
 *
 */
public class TraditionalLayout extends ADatablockLayout {

//    private final DatablockLayoutManager manager;
    private final Font font = new Font("Courier", Font.PLAIN, 11);

    public TraditionalLayout(DatablockLayoutManager manager) {
//        this.manager = manager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getMenuText() {
        return "Traditional (no transponder interaction)";
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public Color getBackgroundColor(GuiRadarContact contact, boolean highlighted) {
        if(highlighted || contact.isIdentActive()) {
            return Color.white;
        }
        return Palette.LANDMASS;
    }

    @Override
    public Color getColor(GuiRadarContact contact) {
        Color color = Palette.RADAR_UNCONTROLLED;

        if(contact.isSelected()) {
            // SELECTED
            color=Palette.RADAR_SELECTED;

        } else if(!contact .isActive()) {
            // INCACTIVE GHOSTS
            color=Palette.RADAR_GHOST;

        } else if(contact.isNeglect()) {
            // BAD GUYS
            color=Palette.RADAR_GHOST;

        } else if(contact.getState()==State.IMPORTANT) {
            // CONTROLLED left column
            color=Palette.RADAR_CONTROLLED;

        } else if(contact.getState()==State.CONTROLLED) {
            // WATCHED middle column
            color=Palette.RADAR_IMPORTANT;
        } else {
            // UNCONTROLLED right column
            color=Palette.RADAR_UNCONTROLLED;
        }

        return color;
    }

    @Override
    public String getDataBlockText(GuiRadarContact c) {
        if(c.isAtc()) {
            return String.format("%s\n%s",c.getCallSign(),c.getAircraft());

        }

        return  String.format("%s %2s",c.getCallSign(),c.getMagnCourse())  +"\n"+
                c.getAircraft()+"\n"+
                String.format("%1s %2s", c.getFlightLevel(),c.getAirSpeed());
    }

    @Override
    public void modify(ContactShape shape, GuiRadarContact c) {

        shape.modify(Type.FilledDot, c, 6);
    }

}