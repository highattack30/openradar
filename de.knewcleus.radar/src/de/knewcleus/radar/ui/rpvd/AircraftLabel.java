package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.radar.autolabel.Label;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.ui.aircraft.ActualLevelLabelElement;
import de.knewcleus.radar.ui.aircraft.AircraftState;
import de.knewcleus.radar.ui.aircraft.AircraftTaskState;
import de.knewcleus.radar.ui.aircraft.CallsignLabelElement;
import de.knewcleus.radar.ui.aircraft.GroundSpeedLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;
import de.knewcleus.radar.ui.labels.ILabelElement;
import de.knewcleus.radar.ui.labels.LabelLine;
import de.knewcleus.radar.ui.labels.MultilineLabel;
import de.knewcleus.radar.ui.labels.StaticTextLabelElement;

public class AircraftLabel implements Label, ILabelDisplay {
	protected final AircraftSymbol associatedSymbol;
	protected double hookX,hookY;
	protected double centerX,centerY;
	protected final List<ILabelElement> labelLines=new ArrayList<ILabelElement>();
	protected final MultilineLabel labelLayout=new MultilineLabel(labelLines);
	
	protected final LabelLine labelLine[]=new LabelLine[5];
	protected final ILabelElement callsignElement;
	protected final ILabelElement nextSectorElement;
	protected final ILabelElement actualLevelElement;
	protected final ILabelElement exitPointElement;
	protected final ILabelElement groundSpeedElement;
	protected final ILabelElement clearedLevelElement;
	protected final ILabelElement exitLevelElement;
	protected final ILabelElement assignedHeadingElement;
	protected final ILabelElement assignedSpeedElement;
	protected final ILabelElement assignedClimbRateElement;
	
	public AircraftLabel(AircraftSymbol associatedSymbol) {
		this.associatedSymbol=associatedSymbol;
		hookX=1.0;
		hookY=1.0;
		
		for (int i=0;i<labelLine.length;i++) {
			labelLine[i]=new LabelLine(new ArrayList<ILabelElement>());
		}
		
		final AircraftState aircraftState=associatedSymbol.getAircraftState();
		callsignElement=new CallsignLabelElement(this,aircraftState);
		nextSectorElement=new StaticTextLabelElement(this,aircraftState);
		actualLevelElement=new ActualLevelLabelElement(this,aircraftState);
		exitPointElement=new StaticTextLabelElement(this,aircraftState);
		groundSpeedElement=new GroundSpeedLabelElement(this,aircraftState);
		clearedLevelElement=new StaticTextLabelElement(this,aircraftState);
		exitLevelElement=new StaticTextLabelElement(this,aircraftState);
		assignedHeadingElement=new StaticTextLabelElement(this,aircraftState);
		assignedSpeedElement=new StaticTextLabelElement(this,aircraftState);
		assignedClimbRateElement=new StaticTextLabelElement(this,aircraftState);
		
		updateLabelContents();
	}
	
	public Rectangle getDisplayBounds() {
		return getBounds2D().getBounds();
	}
	
	@Override
	public RadarPlanViewPanel getDisplayComponent() {
		return associatedSymbol.getRadarPlanViewContext().getRadarPlanViewPanel();
	}
	
	public void processMouseEvent(MouseEvent e) {
		labelLayout.processMouseEvent(e);
	}
	
	public void updateLabelContents() {
		final AircraftState aircraftState=associatedSymbol.getAircraftState();
		labelLines.clear();
		switch (aircraftState.getTaskState()) {
		case OTHER:
			prepareOtherLabel();
			break;
		case NOT_CONCERNED:
			prepareNotConcernedLabel();
			break;
		case ASSUMED:
		case CONCERNED:
			prepareStandardLabel();
			break;
		}
	}
	
	private void prepareOtherLabel() {
		labelLine[0].getElements().clear();
		
		labelLine[0].getElements().add(callsignElement);
		labelLine[0].getElements().add(nextSectorElement);
		
		labelLines.clear();
		labelLines.add(labelLine[0]);
		labelLines.add(actualLevelElement);
	}
	
	private void prepareNotConcernedLabel() {
		labelLine[0].getElements().clear();
		labelLine[1].getElements().clear();
		labelLine[2].getElements().clear();
		labelLine[3].getElements().clear();
		
		labelLine[0].getElements().add(callsignElement);
		labelLine[0].getElements().add(nextSectorElement);
		
		labelLine[1].getElements().add(actualLevelElement);
		labelLine[1].getElements().add(exitPointElement);
		labelLine[1].getElements().add(groundSpeedElement);
		
		labelLine[2].getElements().add(clearedLevelElement);
		labelLine[2].getElements().add(exitLevelElement);
		
		labelLine[3].getElements().add(assignedHeadingElement);
		labelLine[3].getElements().add(assignedSpeedElement);
		labelLine[3].getElements().add(assignedClimbRateElement);
		
		// TODO: fifth line contains optional information...
		
		labelLines.clear();
		labelLines.add(labelLine[0]);
		labelLines.add(labelLine[1]);
		labelLines.add(labelLine[2]);
		labelLines.add(labelLine[3]);
	}
	
	private void prepareStandardLabel() {
		labelLine[0].getElements().clear();
		labelLine[1].getElements().clear();
		labelLine[2].getElements().clear();
		labelLine[3].getElements().clear();
		
		labelLine[0].getElements().add(callsignElement);
		labelLine[0].getElements().add(nextSectorElement);
		
		labelLine[1].getElements().add(actualLevelElement);
		labelLine[1].getElements().add(exitPointElement);
		labelLine[1].getElements().add(groundSpeedElement);
		
		labelLine[2].getElements().add(clearedLevelElement);
		labelLine[2].getElements().add(exitLevelElement);
		
		labelLine[3].getElements().add(assignedHeadingElement);
		labelLine[3].getElements().add(assignedSpeedElement);
		labelLine[3].getElements().add(assignedClimbRateElement);
		
		labelLines.clear();
		labelLines.add(labelLine[0]);
		labelLines.add(labelLine[1]);
		labelLines.add(labelLine[2]);
		labelLines.add(labelLine[3]);
	}
	
	public void layout() {
		labelLayout.layout();
		
		Dimension size=labelLayout.getMinimumSize();
		
		Rectangle2D newBounds=new Rectangle2D.Double(-size.width/2.0,-size.height/2.0,size.width,size.height);
		labelLayout.setBounds(newBounds.getBounds());
	}
	
	public void paint(Graphics2D g2d) {
		Point2D symbolDevicePosition=associatedSymbol.getCurrentDevicePosition();
		double x,y;
		
		x=symbolDevicePosition.getX()+centerX;
		y=symbolDevicePosition.getY()+centerY;
		
		int w,h;
		w=getSize().width;
		h=getSize().height;
		
		g2d.translate(x,y);
		
		final AircraftState aircraftState=associatedSymbol.getAircraftState();
		final AircraftTaskState aircraftTaskState=aircraftState.getTaskState();
		if (aircraftState.isSelected()) {
			g2d.setColor(aircraftTaskState.getSelectedBackgroundColor());
			g2d.fillRect(-w/2,-h/2,w,h);
			g2d.setColor(aircraftTaskState.getSelectedTextColor());
		} else {
			g2d.setColor(aircraftTaskState.getNormalTextColor());
		}
		labelLayout.paint(g2d);
		g2d.translate(-x,-y);
	}
	
	public Rectangle2D getBounds2D() {
		final Point2D symbolDevicePosition=associatedSymbol.getCurrentDevicePosition();
		Dimension size=getSize();
		double x=symbolDevicePosition.getX()+centerX;
		double y=symbolDevicePosition.getY()+centerY;
		Rectangle2D newBounds=new Rectangle2D.Double(x-size.width/2.0,y-size.height/2.0,size.width,size.height);
		
		return newBounds;
	}
	
	public Dimension getSize() {
		return labelLayout.getMinimumSize();
	}
	
	@Override
	public LabeledObject getAssociatedObject() {
		return associatedSymbol;
	}
	
	@Override
	public double getChargeDensity() {
		return 1;
	}
	
	@Override
	public double getHookX() {
		return hookX;
	}
	
	@Override
	public double getHookY() {
		return hookY;
	}
	
	public boolean containsPosition(double x, double y) {
		if (getLeft()<=x && x<=getRight() && getTop()<=y && y<=getBottom())
			return true;
		return false;
	}
	
	@Override
	public void move(double dx, double dy) {
		hookX+=dx;
		hookY+=dy;
		
		final double len=Math.sqrt(hookX*hookX+hookY*hookY);
		final double dirX,dirY;
		
		if (len>1E-3) {
			dirX=hookX/len;
			dirY=hookY/len;
		} else {
			dirX=1.0;
			dirY=0.0;
		}
		
		if (len<AircraftSymbol.minLabelDist) {
			hookX=dirX*AircraftSymbol.minLabelDist;
			hookY=dirY*AircraftSymbol.minLabelDist;
		} else if (len>AircraftSymbol.maxLabelDist) {
			hookX=dirX*AircraftSymbol.maxLabelDist;
			hookY=dirY*AircraftSymbol.maxLabelDist;
		}
		
		Dimension size=getSize();
		centerX=hookX+dirX*size.width/2.0;
		centerY=hookY+dirY*size.height/2.0;
	}

	@Override
	public double getTop() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double h=getSize().height;
		
		return y+centerY-h/2.0;
	}

	@Override
	public double getBottom() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double h=getSize().height;
		
		return y+centerY+h/2.0;
	}

	@Override
	public double getLeft() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double w=getSize().width;
		
		return x+centerX-w/2.0;
	}

	@Override
	public double getRight() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double w=getSize().width;
		
		return x+centerX+w/2.0;
	}
	
	@Override
	public String toString() {
		return "cx="+centerX+" cy="+centerY+" hookX="+hookX+" hookY="+hookY;
	}
}
