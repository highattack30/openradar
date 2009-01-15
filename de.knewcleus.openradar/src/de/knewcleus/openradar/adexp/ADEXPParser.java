package de.knewcleus.openradar.adexp;

import static java.util.Collections.emptySet;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import de.knewcleus.openradar.adexp.fields.BasicFieldDescriptor;
import de.knewcleus.openradar.adexp.impl.ADEXPMessage;
import de.knewcleus.openradar.adexp.impl.AbstractFieldContainer;
import de.knewcleus.openradar.adexp.impl.ListField;
import de.knewcleus.openradar.adexp.impl.StructuredField;

public class ADEXPParser {
	protected final IADEXPMessageDescriptor messageDescriptor;
	
	public ADEXPParser(IADEXPMessageDescriptor messageDescriptor) {
		this.messageDescriptor = messageDescriptor;
	}

	public IADEXPMessage parseMessage(String message) throws IOException, ParserException {
		return parseMessage(new StringReader(message));
	}
	
	public IADEXPMessage parseMessage(Reader reader) throws IOException, ParserException {
		final ADEXPMessage message = new ADEXPMessage(messageDescriptor);
		final Set<String> knownToplevelFields = emptySet();
		final String fieldName = parseFieldContainer(message, messageDescriptor, reader, knownToplevelFields);
		assert(fieldName!=null);
		return message;
	}
	
	protected String parseFieldContainer(AbstractFieldContainer container,
			IFieldContainerDescriptor containerDescriptor,
			Reader reader,
			Set<String> knownToplevelFields) throws IOException, ParserException
	{
		String fieldName = readFieldName(reader);
		final Set<String> knownFields = new HashSet<String>(knownToplevelFields);
		for (IFieldDescriptor descriptor: containerDescriptor) {
			knownFields.add(descriptor.getFieldName());
		}
		while (fieldName!=null) {
			if (knownToplevelFields.contains(fieldName)) {
				/* pass on to toplevel */
				return fieldName;
			}
			if (fieldName.equalsIgnoreCase("BEGIN")) {
				/* a list field */
				fieldName = readKeyword(reader);
				final IListFieldDescriptor descriptor;
				descriptor = (IListFieldDescriptor) containerDescriptor.getFieldDescriptor(fieldName);
				if (!(descriptor instanceof IListFieldDescriptor)) {
					throw new ParserException("Field '"+fieldName+"' is not a list field, but found list field content");
				}
				final IListField field = parseListField(descriptor, reader);
				fieldName = readFieldName(reader);
				container.putField(field);
			} else {
				/* a basic or structured field */
				final IFieldDescriptor descriptor = containerDescriptor.getFieldDescriptor(fieldName);
				if (descriptor instanceof IStructuredFieldDescriptor) {
					/* a structured field */
					final StructuredField field = new StructuredField((IStructuredFieldDescriptor)descriptor);
					fieldName=parseFieldContainer(
							field,
							field.getDescriptor(),
							reader, knownFields);
					container.putField(field);
				} else {
					/* a basic field */
					assert(descriptor instanceof BasicFieldDescriptor);
					int nextChar = skipWhitespace(reader);
					if (nextChar=='-') {
						throw new ParserException("Field '"+fieldName+"' is not a structured field, but it's content starts with a '-'");
					}
					final StringBuilder contentBuilder = new StringBuilder();
					contentBuilder.appendCodePoint(nextChar);
					final String newFieldName;
					newFieldName = parseBasicFieldContent(fieldName, reader, contentBuilder);
					try {
						if (descriptor == null) {
							continue;
						}
						final IBasicFieldDescriptor basicFieldDescriptor = (IBasicFieldDescriptor)descriptor;
						final IFieldParser parser = basicFieldDescriptor.getFieldParser();
						if (parser == null) {
							continue;
						}
						final IBasicField field = parser.parseField(basicFieldDescriptor, contentBuilder.toString());
						container.putField(field);
					} finally {
						fieldName = newFieldName;
					}
				}
			}
		}
		return fieldName;
	}
	
	protected IListField parseListField(IListFieldDescriptor listDescriptor,
			Reader reader) throws IOException, ParserException
	{
		final ListField listField = new ListField(listDescriptor);
		String fieldName = readFieldName(reader);
		final Set<String> knownFields = new HashSet<String>();
		for (IFieldDescriptor descriptor: listDescriptor) {
			knownFields.add(descriptor.getFieldName());
		}
		if (fieldName==null) {
			throw new ParserException("Premature end-of-stream in list field '"+listDescriptor.getFieldName()+"'");
		}
		while (fieldName!=null && !fieldName.equalsIgnoreCase("END")) {
			if (fieldName.equalsIgnoreCase("BEGIN")) {
				/* a list field */
				fieldName = readKeyword(reader);
				final IListFieldDescriptor descriptor;
				descriptor = (IListFieldDescriptor) listDescriptor.getFieldDescriptor(fieldName);
				if (!(descriptor instanceof IListFieldDescriptor)) {
					throw new ParserException("Field '"+fieldName+"' is not a list field, but found list field content");
				}
				final IListField field = parseListField(descriptor, reader);
				fieldName = readFieldName(reader);
				listField.append(field);
			} else {
				/* a basic or structured field */
				final IFieldDescriptor descriptor = listDescriptor.getFieldDescriptor(fieldName);
				
				if (descriptor instanceof IStructuredFieldDescriptor) {
					/* a structured field */
					final StructuredField field = new StructuredField((IStructuredFieldDescriptor)descriptor);
					fieldName=parseFieldContainer(
							field,
							field.getDescriptor(),
							reader, knownFields);
					listField.append(field);
				} else {
					/* a basic field */
					assert(descriptor instanceof BasicFieldDescriptor);
					int nextChar = skipWhitespace(reader);
					if (nextChar=='-') {
						throw new ParserException("Field '"+fieldName+"' is not a structured field, but it's content starts with a '-'");
					}
					final StringBuilder contentBuilder = new StringBuilder();
					contentBuilder.appendCodePoint(nextChar);
					final String newFieldName;
					newFieldName = parseBasicFieldContent(fieldName, reader, contentBuilder);
					try {
						if (descriptor == null) {
							continue;
						}
						final IBasicFieldDescriptor basicFieldDescriptor = (IBasicFieldDescriptor)descriptor;
						final IFieldParser parser = basicFieldDescriptor.getFieldParser();
						if (parser == null) {
							continue;
						}
						final IBasicField field = parser.parseField(basicFieldDescriptor, contentBuilder.toString());
						listField.append(field);
					} finally {
						fieldName = newFieldName;
					}
				}
			}
		}
		if (fieldName==null) {
			throw new ParserException("Premature end-of-stream in list field '"+listDescriptor.getFieldName()+"'");
		}
		assert(fieldName.equalsIgnoreCase("END"));
		fieldName = readKeyword(reader);
		if (!fieldName.equalsIgnoreCase(listDescriptor.getFieldName())) {
			throw new ParserException("Name mismatch at list field END: '"+listDescriptor.getFieldName()+"' expected, got '"+fieldName+"'");
		}
		return listField;
	}
	
	protected String parseBasicFieldContent(String fieldName, Reader reader, StringBuilder contentBuilder)
		throws IOException, ParserException
	{
		boolean seenWhitespace = false;
		int nextChar = reader.read();
		while (nextChar!=-1) {
			if (Character.isWhitespace(nextChar)) {
				seenWhitespace = true;
			}
			if (seenWhitespace && nextChar == '-') {
				/* save the skipped characters */
				final StringBuilder skipBuilder = new StringBuilder();
				skipBuilder.appendCodePoint(nextChar);
				int skipChar = reader.read();
				while (Character.isWhitespace(skipChar)) {
					skipBuilder.appendCodePoint(skipChar);
					skipChar = reader.read();
				}
				if (!Character.isLetterOrDigit(skipChar)) {
					/* not a keyword */
					contentBuilder.append(skipBuilder);
					nextChar = skipChar;
					seenWhitespace = Character.isWhitespace(nextChar);
					continue;
				}
				/* might be a keyword */
				final int keywordStart = skipBuilder.length();
				while (Character.isLetterOrDigit(skipChar)) {
					skipBuilder.appendCodePoint(skipChar);
					skipChar = reader.read();
				}
				if (!Character.isWhitespace(skipChar)) {
					/* no whitespace separation => not a keyword */
					contentBuilder.append(skipBuilder);
					nextChar = skipChar;
					seenWhitespace = false;
					continue;
				}
				/* we have a keyword, so stop here and pass the field name on */
				final String newFieldName = skipBuilder.substring(keywordStart);
				return newFieldName;
			}
			if (!Character.isWhitespace(nextChar)) {
				seenWhitespace = false;
			}
			contentBuilder.appendCodePoint(nextChar);
			nextChar = reader.read();
		}
		return null;
	}
	
	protected int skipWhitespace(Reader reader) throws IOException {
		int nextChar;
		do {
			nextChar = reader.read();
		} while (Character.isWhitespace(nextChar));
		return nextChar;
	}
	
	protected String readKeyword(Reader reader) throws IOException, ParserException {
		final StringBuilder keywordBuilder = new StringBuilder();
		int nextChar = skipWhitespace(reader);
		if (nextChar==-1) {
			throw new ParserException("Premature end-of-stream where keyword is expected");
		}
		if (!Character.isLetterOrDigit(nextChar)) {
			throw new ParserException("Keyword expected after start-of-field character '-', but got '"+(char)nextChar+"'");
		}
		while (Character.isLetterOrDigit(nextChar)) {
			keywordBuilder.appendCodePoint(nextChar);
			nextChar = reader.read();
		}
		if (nextChar==-1) {
			throw new ParserException("Premature end-of-stream after keyword '"+keywordBuilder+"'");
		}
		if (!Character.isWhitespace(nextChar)) {
			throw new ParserException("Separator expected after keyword, but got '"+(char)nextChar+"'");
		}
		return keywordBuilder.toString();
	}
	
	protected String readFieldName(Reader reader) throws IOException, ParserException {
		int nextChar;
		nextChar = skipWhitespace(reader);
		if (nextChar==-1) {
			return null;
		}
		if (nextChar!='-') {
			throw new ParserException("Expected start-of-field character '-', but got '"+(char)nextChar+"'");
		}
		return readKeyword(reader);
	}
}