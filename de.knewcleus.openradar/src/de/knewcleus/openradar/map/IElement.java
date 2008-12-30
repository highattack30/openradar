package de.knewcleus.openradar.map;

/**
 * An element is a view that is contained in an element container.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IElement extends IView {
	public IContainer getContainer();
}
