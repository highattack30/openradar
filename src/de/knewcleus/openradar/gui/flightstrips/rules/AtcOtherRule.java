package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AtcOtherRule extends AbstractRule {

	private final String OtherAtc; // if empty: any other ATC
	
	public AtcOtherRule(String OtherAtc) {
		this.OtherAtc = OtherAtc;
	}
	
	public AtcOtherRule(Element element, LogicManager logic) {
		this.OtherAtc = element.getAttributeValue("otheratc");
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		return (flightplan != null) && ((OtherAtc.length() <= 0) ? flightplan.isOwnedBySomeoneElse() : OtherAtc.equalsIgnoreCase(flightplan.getOwner()));
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is controlled by " + ((OtherAtc.length() > 0) ? OtherAtc : "other ATC") + ".");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("otheratc", OtherAtc);
	}

}