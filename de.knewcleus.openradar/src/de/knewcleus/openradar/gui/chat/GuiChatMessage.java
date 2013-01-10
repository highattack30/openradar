/**
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
package de.knewcleus.openradar.gui.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

/**
 * The front end data object for a chat message.
 * 
 * @author Wolfram Wagner
 */
public class GuiChatMessage {

    private GuiRadarContact knownRadarContact = null;
    
    private Date created = null;
    private String timestamp = null;
    private String callSign = null;
    private String message = null;
    private String frequency = null;
    private boolean airportMentioned = false;
    private boolean isOwnMessage = false;
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
    
    public GuiChatMessage(GuiMasterController master, Date time, String callSign, String frequency, String message) {
        this.created = time;
        timestamp = sdf.format(time);
        this.callSign = callSign;
        this.message = message;
        this.frequency = frequency;
        this.airportMentioned = message.contains(master.getDataRegistry().getAirportCode());
        this.isOwnMessage = callSign.contains(master.getCurrentATCCallSign());
    }
    
    public Date getCreated() { 
        return created; 
    }
    
    public GuiRadarContact getKnownRadarContact() {
        return knownRadarContact;
    }

    public void setKnownRadarContact(GuiRadarContact knownRadarContact) {
        this.knownRadarContact = knownRadarContact;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCallSign() {
        return  callSign;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAirportMentioned() {
        return airportMentioned;
    }

    public boolean isContactSelected() {
        return knownRadarContact!=null && knownRadarContact.isSelected();
    }

    public boolean isOwnMessage() {
        return isOwnMessage;
    }

    public String getFrequency() {
        return frequency;
    }
    
    public String toString() {
        return message;
    }

    public boolean isNeglectOrInactive() {
        return (knownRadarContact!=null && knownRadarContact.isNeglect()) || (knownRadarContact!=null && !knownRadarContact.isActive());
    }

    public boolean messageContainsSelectedContact(GuiMasterController master) {
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        if(selectedContact==null) {
            return false; 
        } else {
            return message.contains(selectedContact.getCallSign());
        }
    }
}